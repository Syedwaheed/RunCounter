package com.edu.core.data.run

import com.edu.core.database.dao.DeletedRunSyncDao
import com.edu.core.database.dao.RunPendingSyncDao
import com.edu.core.database.entity.DeletedRunSyncEntity
import com.edu.core.database.entity.RunPendingSyncEntity
import com.edu.core.database.mappers.toRun
import com.edu.core.database.mappers.toRunEntity
import com.edu.core.domain.SessionStorage
import com.edu.core.domain.run.LocalRunDataSource
import com.edu.core.domain.run.RemoteImageStorage
import com.edu.core.domain.run.RemoteRunDataSource
import com.edu.core.domain.run.Run
import com.edu.core.domain.run.RunId
import com.edu.core.domain.run.RunRepository
import com.edu.core.domain.util.DataError
import com.edu.core.domain.util.EmptyResult
import com.edu.core.domain.util.Result
import com.edu.core.domain.util.asEmptyDataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class OfflineFirstRunRepository(
    private val localRunDataSource: LocalRunDataSource,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val remoteImageStorage: RemoteImageStorage,
    private val applicationScope: CoroutineScope,
    private val runPendingSyncDao: RunPendingSyncDao,
    private val deletedRunSyncDao: DeletedRunSyncDao,
    private val sessionStorage: SessionStorage
): RunRepository{
    override fun getRuns(): Flow<List<Run>> {
        return localRunDataSource.getRuns()
    }

    override suspend fun fetchRuns(): EmptyResult<DataError> {
        return when(val result = remoteRunDataSource.getRuns()){
            is Result.Success -> {
                Timber.d("fetchRuns: Server returned ${result.data.size} runs")
                applicationScope.async {
                    // Get current local runs to preserve goalId and Firebase image URLs
                    val localRuns = localRunDataSource.getRuns().first()
                    Timber.d("fetchRuns: Local has ${localRuns.size} runs before clear")
                    val localGoalIdMap = localRuns.associate { it.id to it.goalId }
                    val localMapPictureUrlMap = localRuns.associate { it.id to it.mapPictureUrl }

                    // Merge backend runs with local goalId and mapPictureUrl values
                    val runsWithPreservedGoals = result.data.map { backendRun ->
                        val localGoalId = localGoalIdMap[backendRun.id]
                        val localMapPictureUrl = localMapPictureUrlMap[backendRun.id]
                        backendRun.copy(
                            goalId = if (localGoalId != null && backendRun.goalId == null) localGoalId else backendRun.goalId,
                            mapPictureUrl = localMapPictureUrl ?: backendRun.mapPictureUrl
                        )
                    }

                    // Clear old runs before saving new user's runs
                    // This ensures we don't mix data between different users
                    localRunDataSource.deleteAllRuns()
                    Timber.d("fetchRuns: Cleared local runs, now upserting ${runsWithPreservedGoals.size} runs")

                    val upsertResult = localRunDataSource.upsertRuns(runsWithPreservedGoals)
                    Timber.d("fetchRuns: Upsert result = $upsertResult")
                    upsertResult.asEmptyDataResult()
                }.await()
            }
            is Result.Error -> {
                Timber.e("fetchRuns: Server error = ${result.error}")
                result.asEmptyDataResult()
            }
        }
    }

    override suspend fun upsertRun(
        run: Run,
        mapPicture: ByteArray
    ): EmptyResult<DataError> {
        // First, save run locally to get the ID
        val localResult = localRunDataSource.upsertRun(run)
        if (localResult !is Result.Success) {
            return localResult.asEmptyDataResult()
        }
        val localRunId = localResult.data

        // Upload image to Firebase Storage to get the download URL
        val imageUploadResult = remoteImageStorage.uploadImage(localRunId, mapPicture)
        val firebaseImageUrl = when (imageUploadResult) {
            is Result.Success -> {
                Timber.d("Image uploaded to Firebase: ${imageUploadResult.data}")
                imageUploadResult.data
            }
            is Result.Error -> {
                Timber.e("Failed to upload image to Firebase: ${imageUploadResult.error}")
                null
            }
        }

        // Update local run with the Firebase image URL
        val runWithImageUrl = run.copy(id = localRunId, mapPictureUrl = firebaseImageUrl)
        localRunDataSource.upsertRun(runWithImageUrl)

        // Sync with backend (still sends image bytes as backend requires it)
        val remoteResult = remoteRunDataSource.postRun(
            run = runWithImageUrl,
            mapPicture = mapPicture
        )
        return when (remoteResult) {
            is Result.Error -> {
                applicationScope.async {
                    val userId = sessionStorage.get()?.userId ?: return@async Result.Success(Unit)
                    runPendingSyncDao.upsertRunPendingSyncEntity(
                        RunPendingSyncEntity(
                            run = runWithImageUrl.toRunEntity(),
                            mapPictureByte = mapPicture,
                            userId = userId
                        )
                    )
                    Result.Success(Unit)
                }.await()
            }
            is Result.Success -> {
                applicationScope.async {
                    val serverRun = remoteResult.data
                    // Always use Firebase URL for display, preserve goalId
                    val runToSave = serverRun.copy(
                        goalId = run.goalId,
                        mapPictureUrl = firebaseImageUrl ?: serverRun.mapPictureUrl
                    )
                    if (serverRun.id != localRunId) {
                        localRunDataSource.deleteRun(localRunId)
                    }
                    localRunDataSource.upsertRun(runToSave).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun deleteAllRuns() {
        localRunDataSource.deleteAllRuns()
    }

    override suspend fun deleteRun(id: RunId) {
        localRunDataSource.deleteRun(id)

        // Delete image from Firebase Storage
        remoteImageStorage.deleteImage(id)

        // Check if this run was pending sync (not yet uploaded to server)
        val pendingSyncEntity = runPendingSyncDao.getRunPendingSyncEntity(id)
        if (pendingSyncEntity != null) {
            // Run was never synced to server, just remove from pending
            runPendingSyncDao.deleteRunPendingSyncEntity(id)
            return
        }
        // Run exists on server, try to delete remotely
        val remoteResult = applicationScope.async {
            remoteRunDataSource.deleteRun(id)
        }.await()
        // If remote delete fails, store in deleted sync table for later
        if (remoteResult is Result.Error) {
            val userId = sessionStorage.get()?.userId ?: return
            deletedRunSyncDao.upsertDeletedRunSyncEntity(
                DeletedRunSyncEntity(
                    runId = id,
                    userId = userId
                )
            )
        }
    }

    override suspend fun syncPendingRuns() {
        withContext(Dispatchers.IO) {
            val userId = sessionStorage.get()?.userId ?: return@withContext
            val createdSynEntity = async {
                runPendingSyncDao.getAllRunPendingSyncEntities(userId)
            }
            val deletedSyncEntities = async {
                deletedRunSyncDao.getAllDeletedRunSyncEntities(userId)
            }
            val createJobs = createdSynEntity
                .await()
                .map { entity ->
                    launch {
                        val run = entity.run.toRun()

                        // Upload image to Firebase if not already uploaded
                        val firebaseImageUrl = if (run.mapPictureUrl == null) {
                            when (val imageResult = remoteImageStorage.uploadImage(entity.run.id, entity.mapPictureByte)) {
                                is Result.Success -> imageResult.data
                                is Result.Error -> null
                            }
                        } else {
                            run.mapPictureUrl
                        }

                        val runWithImageUrl = run.copy(mapPictureUrl = firebaseImageUrl)

                        when (val result = remoteRunDataSource.postRun(runWithImageUrl, entity.mapPictureByte)) {
                            is Result.Error -> Unit
                            is Result.Success -> {
                                applicationScope.launch {
                                    val serverRun = result.data
                                    // Preserve goalId and Firebase URL
                                    val runToSave = serverRun.copy(
                                        goalId = run.goalId,
                                        mapPictureUrl = firebaseImageUrl ?: serverRun.mapPictureUrl
                                    )
                                    if (serverRun.id != entity.run.id) {
                                        localRunDataSource.deleteRun(entity.run.id)
                                    }
                                    localRunDataSource.upsertRun(runToSave)
                                    runPendingSyncDao.deleteRunPendingSyncEntity(entity.run.id)
                                }.join()
                            }
                        }
                    }
                }
            val deletedJobs = deletedSyncEntities
                .await()
                .map {
                    launch {
                        when (remoteRunDataSource.deleteRun(it.runId)) {
                            is Result.Error -> Unit
                            is Result.Success -> {
                                applicationScope.launch {
                                    // Also delete from Firebase
                                    remoteImageStorage.deleteImage(it.runId)
                                    deletedRunSyncDao.deleteDeletedRunSyncEntity(it.runId)
                                }.join()
                            }
                        }
                    }
                }
            createJobs.joinAll()
            deletedJobs.joinAll()
        }
    }
}