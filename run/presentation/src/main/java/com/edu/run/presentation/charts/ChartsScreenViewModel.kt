package com.edu.run.presentation.charts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.core.domain.run.RunRepository
import com.edu.goal.domain.GoalRepository
import com.edu.run.presentation.charts.mappers.toChartLabel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ChartsScreenViewModel(
    private val runRepository: RunRepository,
    private val goalRepository: GoalRepository
) : ViewModel() {

    var state by mutableStateOf(ChartsScreenState())
        private set

    private val selectedGoalIdFlow = MutableStateFlow<String?>(null)

    init {
        combine(
            runRepository.getRuns(),
            goalRepository.getGoals(),
            selectedGoalIdFlow
        ) { runs, goals, selectedGoalId ->
            Triple(runs, goals, selectedGoalId)
        }.onEach { (runs, goals, selectedGoalId) ->
            // Filter runs by selected goal if one is selected
            val filteredRuns = if (selectedGoalId != null) {
                runs.filter { it.goalId == selectedGoalId }
            } else {
                runs
            }

            val sortedRuns = filteredRuns.sortedBy { it.dateTimeUtc }
            val distance = sortedRuns.map { it.distanceMeters / 1000f }
            val speed = sortedRuns.map { it.averageSpeedKmh.toFloat() }
            val labels = sortedRuns.map { it.toChartLabel() }

            // Calculate goal progress from actual runs
            val goalProgressData = goals.map { goal ->
                val goalRuns = runs.filter { it.goalId == goal.id }
                val totalDistanceMeters = goalRuns.sumOf { it.distanceMeters }
                val currentDistanceKm = totalDistanceMeters / 1000f
                val targetDistanceKm = goal.targetDistanceMeters / 1000f
                val progressPercentage = if (goal.targetDistanceMeters > 0) {
                    (totalDistanceMeters.toFloat() / goal.targetDistanceMeters).coerceIn(0f, 1f)
                } else 0f

                GoalProgressData(
                    goalId = goal.id,
                    goalName = goal.name,
                    targetDistanceKm = targetDistanceKm,
                    currentDistanceKm = currentDistanceKm,
                    progressPercentage = progressPercentage,
                    isCompleted = progressPercentage >= 1f || goal.isCompleted,
                    isExpired = goal.isExpired
                )
            }

            state = state.copy(
                distancePerRun = distance,
                pacePerRun = speed,
                runLabels = labels,
                goalProgressData = goalProgressData,
                selectedGoalId = selectedGoalId,
                isLoading = false
            )
        }.launchIn(viewModelScope)
    }

    fun onAction(action: ChartsScreenAction) {
        when (action) {
            is ChartsScreenAction.SelectGoal -> {
                selectedGoalIdFlow.value = action.goalId
            }
            ChartsScreenAction.ClearGoalFilter -> {
                selectedGoalIdFlow.value = null
            }
        }
    }
}