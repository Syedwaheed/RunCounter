package com.edu.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.edu.core.database.entity.DeletedRunSyncEntity
import com.edu.core.database.entity.RunPendingSyncEntity

@Dao
interface RunPendingSyncDao {

    @Query("Select * from runpendingsyncentity where userId = :userId")
    suspend fun getAllRunPendingSyncEntities(userId: String): List<RunPendingSyncEntity>

    @Query("Select * from runpendingsyncentity where id = :runId")
    suspend fun getRunPendingSyncEntity(runId: String): RunPendingSyncEntity?

    @Upsert
    suspend fun upsertRunPendingSyncEntity(runPendingSyncEntity: RunPendingSyncEntity)

    @Query("DELETE FROM runpendingsyncentity WHERE id = :runId")
    suspend fun deleteRunPendingSyncEntity(runId: String)

    @Query("DELETE FROM runpendingsyncentity")
    suspend fun deleteAll()
}
