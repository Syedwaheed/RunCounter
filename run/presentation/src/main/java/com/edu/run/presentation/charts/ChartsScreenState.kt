package com.edu.run.presentation.charts

data class ChartsScreenState(
    val distancePerRun: List<Float> = emptyList(),
    val pacePerRun: List<Float> = emptyList(),
    val runLabels: List<String> = emptyList(),
    val goalProgressData: List<GoalProgressData> = emptyList(),
    val selectedGoalId: String? = null,
    val isLoading: Boolean = true
)

data class GoalProgressData(
    val goalId: String,
    val goalName: String,
    val targetDistanceKm: Float,
    val currentDistanceKm: Float,
    val progressPercentage: Float,
    val isCompleted: Boolean,
    val isExpired: Boolean
)