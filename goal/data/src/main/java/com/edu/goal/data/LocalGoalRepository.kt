package com.edu.goal.data

import com.edu.core.database.dao.GoalDao
import com.edu.core.database.dao.RunDao
import com.edu.core.domain.SessionStorage
import com.edu.goal.data.mappers.toGoal
import com.edu.goal.data.mappers.toGoalEntity
import com.edu.goal.domain.Goal
import com.edu.goal.domain.GoalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class LocalGoalRepository(
    private val goalDao: GoalDao,
    private val runDao: RunDao,
    private val sessionStorage: SessionStorage
) : GoalRepository {

    private suspend fun getCurrentUserId(): String? {
        return sessionStorage.get()?.userId
    }

    override fun getGoals(): Flow<List<Goal>> {
        return flow { emit(getCurrentUserId()) }
            .flatMapLatest { userId ->
                if (userId == null) {
                    emptyFlow()
                } else {
                    goalDao.getAllGoals(userId).map { goalsEntityList ->
                        goalsEntityList.map { it.toGoal() }
                    }
                }
            }
    }

    override suspend fun upsertGoal(goal: Goal) {
        val userId = getCurrentUserId() ?: return
        goalDao.upsertGoal(goal.toGoalEntity(userId))
    }

    override suspend fun deleteGoal(id: String) {
        goalDao.deleteGoalById(id = id)
    }

    override suspend fun deleteAllGoals() {
        val userId = getCurrentUserId() ?: return
        goalDao.deleteAllGoals(userId)
    }

    override suspend fun getGoalById(id: String): Goal? {
        return goalDao.getGoalById(id = id)?.toGoal()
    }

    override fun getGoalProgress(goalId: String): Flow<Long> {
        return runDao.getTotalDistanceByGoal(goalId).map { it ?: 0L }
    }

    override fun getAvailableGoals(): Flow<List<Goal>> {
        return flow { emit(getCurrentUserId()) }
            .flatMapLatest { userId ->
                if (userId == null) {
                    emptyFlow()
                } else {
                    goalDao.getAllGoals(userId).map { goalsEntityList ->
                        goalsEntityList
                            .map { it.toGoal() }
                            .filter { goal ->
                                !goal.isCompleted && !goal.isExpired
                            }
                    }
                }
            }
    }
}