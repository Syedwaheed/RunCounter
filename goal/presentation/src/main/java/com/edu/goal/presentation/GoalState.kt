package com.edu.goal.presentation

import androidx.compose.foundation.text.input.TextFieldState
import com.edu.goal.presentation.model.GoalUI
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate

data class GoalState(
    val goals: PersistentList<GoalUI> = persistentListOf(),
    val isLoading: Boolean = false,
    val nameState: TextFieldState = TextFieldState(),
    val targetState: TextFieldState = TextFieldState(),
    val newGoalEndDate: LocalDate? = null,
    val formattedGoalEndDate: String = "",
    val showDatePicker: Boolean = false,
    val dateError: String? = null,
    val nameError: String? = null,
    val targetError: String? = null,
    val isSaving: Boolean = false,
    val goalSaved: Boolean = false
)