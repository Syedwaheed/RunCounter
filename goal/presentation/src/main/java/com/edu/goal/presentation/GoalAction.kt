package com.edu.goal.presentation

import com.edu.goal.domain.Goal
import java.time.LocalDate
import java.time.LocalTime


sealed interface GoalAction {
    data object OnAddGoalClick : GoalAction
    data object OnDismissAddGoalSheet : GoalAction
    data object OnSaveGoalClick : GoalAction
    data class OnGoalEndDateChange(val date: LocalDate) : GoalAction
    data class OnGoalClick(val goal: Goal) : GoalAction
    data class OnDeleteGoalClick(val id: String): GoalAction
    data class OnEditGoalClick(val id: String): GoalAction
    data object ShowDatePickerDialog : GoalAction
    data object HideDatePickerDialog : GoalAction
    data class OnDateSelected(val date: LocalDate) : GoalAction
}