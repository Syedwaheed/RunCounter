package com.edu.goal.presentation

import java.time.LocalDate

sealed interface GoalAction {
    data object OnSaveGoalClick : GoalAction
    data class OnDeleteGoalClick(val id: String) : GoalAction
    data object ShowDatePickerDialog : GoalAction
    data object HideDatePickerDialog : GoalAction
    data class OnDateSelected(val date: LocalDate) : GoalAction
    data object OnGoalSavedHandled : GoalAction
}