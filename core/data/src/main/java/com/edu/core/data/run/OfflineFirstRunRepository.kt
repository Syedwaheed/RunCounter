package com.edu.core.data.run

import com.edu.core.database.dao.DeletedRunSyncDao
import com.edu.core.database.dao.RunPendingSyncDao
import com.edu.core.database.entity.DeletedRunSyncEntity
import com.edu.core.database.entity.RunPendingSyncEntity
import com.edu.core.database.mappers.toRun
import com.edu.core.database.mappers.toRunEntity
import com.edu.core.domain.SessionStorage
import com.edu.core.domain.run.LocalRunDataSource
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

class OfflineFirstRunRepository(
    private val localRunDataSource: LocalRunDataSource,
    private val remoteRunDataSource: RemoteRunDataSource,
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
                applicationScope.async {
                    // Get current local runs to preserve goalId (backend doesn't store goals)
                    val localRuns = localRunDataSource.getRuns().first()
                    val localGoalIdMap = localRuns.associate { it.id to it.goalId }

                    // Merge backend runs with local goalId values
                    val runsWithPreservedGoals = result.data.map { backendRun ->
                        val localGoalId = localGoalIdMap[backendRun.id]
                        if (localGoalId != null && backendRun.goalId == null) {
                            backendRun.copy(goalId = localGoalId)
                        } else {
                            backendRun
                        }
                    }
                    localRunDataSource.upsertRuns(runsWithPreservedGoals).asEmptyDataResult()
                }.await()
            }
            is Result.Error -> {
                result.asEmptyDataResult()
            }
        }
    }

    override suspend fun upsertRun(
        run: Run,
        mapPicture: ByteArray
    ): EmptyResult<DataError> {
        val localResult = localRunDataSource.upsertRun(run)
        if(localResult !is Result.Success){
            return localResult.asEmptyDataResult()
        }
        val localRunId = localResult.data
        val runWithId = run.copy(id = localRunId)
        val remoteResult = remoteRunDataSource.postRun(
            run = runWithId,
            mapPicture = mapPicture
        )
        return when (remoteResult){
            is Result.Error -> {
                applicationScope.async {
                    val userId = sessionStorage.get()?.userId ?: return@async Result.Success(Unit)
                    runPendingSyncDao.upsertRunPendingSyncEntity(
                        RunPendingSyncEntity(
                            run = runWithId.toRunEntity(),
                            mapPictureByte = mapPicture,
                            userId = userId
                        )
                    )
                    Result.Success(Unit)
                }.await()
            }
            is Result.Success ->{
                applicationScope.async {
                    val serverRun = remoteResult.data
                    // Preserve goalId from local run since server might not return it
                    val runToSave = serverRun.copy(goalId = run.goalId)
                    if(serverRun.id != localRunId) {
                        localRunDataSource.deleteRun(localRunId)
                    }
                    localRunDataSource.upsertRun(runToSave).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun deleteRun(id: RunId) {
        localRunDataSource.deleteRun(id)
        // Check if this run was pending sync (not yet uploaded to server)
        val pendingSyncEntity = runPendingSyncDao.getRunPendingSyncEntity(id)
        if(pendingSyncEntity != null) {
            // Run was never synced to server, just remove from pending
            runPendingSyncDao.deleteRunPendingSyncEntity(id)
            return
        }
        // Run exists on server, try to delete remotely
        val remoteResult = applicationScope.async {
            remoteRunDataSource.deleteRun(id)
        }.await()
        // If remote delete fails, store in deleted sync table for later
        if(remoteResult is Result.Error) {
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
        withContext(Dispatchers.IO){
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
                        when(val result = remoteRunDataSource.postRun(run, entity.mapPictureByte)){
                            is Result.Error -> Unit
                            is Result.Success -> {
                                applicationScope.launch {
                                    val serverRun = result.data
                                    // Preserve goalId from local run since server might not return it
                                    val runToSave = serverRun.copy(goalId = run.goalId)
                                    if(serverRun.id != entity.run.id) {
                                        localRunDataSource.deleteRun(entity.run.id)
                                        localRunDataSource.upsertRun(runToSave)
                                    }
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
                        when(remoteRunDataSource.deleteRun(it.runId)){
                            is Result.Error -> Unit
                            is Result.Success -> {
                                applicationScope.launch {
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