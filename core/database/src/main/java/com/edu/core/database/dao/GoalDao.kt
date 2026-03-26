package com.edu.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.edu.core.database.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM GoalEntity WHERE userId = :userId")
    fun getAllGoals(userId: String): Flow<List<GoalEntity>>

    @Query("DELETE FROM GoalEntity WHERE userId = :userId")
    suspend fun deleteAllGoals(userId: String)

    @Upsert
    suspend fun upsertGoal(goalEntity: GoalEntity)

    @Query("SELECT * FROM GoalEntity WHERE id = :id")
    suspend fun getGoalById(id: String): GoalEntity?

    @Query("DELETE FROM GoalEntity WHERE id = :id")
    suspend fun deleteGoalById(id: String)

}