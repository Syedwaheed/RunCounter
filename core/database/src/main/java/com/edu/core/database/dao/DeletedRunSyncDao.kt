package com.edu.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.edu.core.database.entity.DeletedRunSyncEntity

@Dao
interface DeletedRunSyncDao {

    @Query("SELECT * FROM deletedrunsyncentity WHERE userId = :userId")
    suspend fun getAllDeletedRunSyncEntities(userId: String): List<DeletedRunSyncEntity>

    @Upsert
    suspend fun upsertDeletedRunSyncEntity(deletedRunSyncEntity: DeletedRunSyncEntity)

    @Query("DELETE FROM deletedrunsyncentity WHERE runId = :runId")
    suspend fun deleteDeletedRunSyncEntity(runId: String)

    @Query("DELETE FROM deletedrunsyncentity")
    suspend fun deleteAll()
}