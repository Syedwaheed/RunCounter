package com.edu.goal.domain

import kotlinx.coroutines.flow.Flow

interface GoalRepository {

    fun getGoals() : Flow<List<Goal>>

    suspend fun upsertGoal(goal: Goal) : Unit

    suspend fun deleteGoal(id: String)

    suspend fun getGoalById(id: String) : Goal?

    fun getGoalProgress(goalId: String): Flow<Long>

    fun getAvailableGoals(): Flow<List<Goal>>
}