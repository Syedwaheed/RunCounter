package com.edu.goal.data

import com.edu.core.database.dao.GoalDao
import com.edu.core.database.dao.RunDao
import com.edu.goal.data.mappers.toGoal
import com.edu.goal.data.mappers.toGoalEntity
import com.edu.goal.domain.Goal
import com.edu.goal.domain.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime

class LocalGoalRepository(
    private val goalDao: GoalDao,
    private val runDao: RunDao
) : GoalRepository {
    override fun getGoals(): Flow<List<Goal>> {
        return goalDao.getAllGoals().map { goalsEntityList->
            goalsEntityList.map {
                it.toGoal()
            }
        }
    }

    override suspend fun upsertGoal(goal: Goal) {
        goalDao.upsertGoal(goal.toGoalEntity())
    }

    override suspend fun deleteGoal(id: String) {
        goalDao.deleteGoalById(id = id)
    }

    override suspend fun getGoalById(id: String): Goal? {
        return goalDao.getGoalById(id = id)?.toGoal()
    }

    override fun getGoalProgress(goalId: String): Flow<Long> {
        return runDao.getTotalDistanceByGoal(goalId).map { it ?: 0L }
    }

    override fun getAvailableGoals(): Flow<List<Goal>> {
        return goalDao.getAllGoals().map { goalsEntityList ->
            val now = ZonedDateTime.now()
            goalsEntityList
                .map { it.toGoal() }
                .filter { goal ->
                    !goal.isCompleted && !goal.isExpired
                }
        }
    }
}