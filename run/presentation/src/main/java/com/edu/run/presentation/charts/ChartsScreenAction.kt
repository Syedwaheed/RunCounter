package com.edu.run.presentation.charts

sealed interface ChartsScreenAction {
    data class SelectGoal(val goalId: String) : ChartsScreenAction
    data object ClearGoalFilter : ChartsScreenAction
}