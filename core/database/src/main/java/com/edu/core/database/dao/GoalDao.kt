package com.edu.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.edu.core.database.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("Select * from GoalEntity")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Query("Delete from GoalEntity")
    suspend fun deleteAllGoals()

    @Upsert
    suspend fun upsertGoal(goalEntity: GoalEntity)

    @Query("SELECT * FROM GoalEntity WHERE id =:id")
    suspend fun getGoalById(id: String): GoalEntity?

    @Query("DELETE FROM GoalEntity WHERE id = :id")
    suspend fun deleteGoalById(id: String)

}