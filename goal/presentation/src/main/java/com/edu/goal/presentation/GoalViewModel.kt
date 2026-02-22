package com.edu.goal.presentation

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.goal.domain.GoalRepository
import com.edu.goal.presentation.mappers.toGoalUI
import com.edu.goal.domain.DateFormatter
import com.edu.goal.domain.Goal
import com.edu.goal.domain.GoalValidationResult
import com.edu.goal.domain.DefaultGoalValidator
import com.edu.goal.domain.GoalValidator
import com.edu.goal.presentation.mappers.toZonedDateTime
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class GoalViewModel(
    private val goalRepository: GoalRepository,
    private val dateFormatter: DateFormatter,
    private val goalValidator: GoalValidator
) : ViewModel() {

    var state by mutableStateOf(GoalState())
        private set
    init {
        goalRepository.getGoals()
            .flatMapLatest { goals ->
                if (goals.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    // Combine each goal with its actual progress from linked runs
                    val progressFlows = goals.map { goal ->
                        goalRepository.getGoalProgress(goal.id).map { progress ->
                            goal.copy(currentDistanceMeters = progress)
                        }
                    }
                    combine(progressFlows) { it.toList() }
                }
            }
            .onEach { goalsWithProgress ->
                state = state.copy(
                    goals = goalsWithProgress.map { it.toGoalUI() },
                    isLoading = false
                )
            }
            .launchIn(viewModelScope)

        // Clear name error when user types
        snapshotFlow { state.nameState.text }
            .onEach { if (state.nameError != null) state = state.copy(nameError = null) }
            .launchIn(viewModelScope)

        // Clear target error when user types
        snapshotFlow { state.targetState.text }
            .onEach { if (state.targetError != null) state = state.copy(targetError = null) }
            .launchIn(viewModelScope)
    }


    fun onAction(goalAction: GoalAction) {
        when (goalAction) {
            GoalAction.OnAddGoalClick -> TODO()
            is GoalAction.OnDeleteGoalClick -> deleteGoal(goalAction.id)
            is GoalAction.OnEditGoalClick -> TODO()
            is GoalAction.OnGoalClick -> TODO()
            GoalAction.OnDismissAddGoalSheet -> TODO()
            is GoalAction.OnGoalEndDateChange -> TODO()
            GoalAction.OnSaveGoalClick -> {
                val dateResult = goalValidator.validateEndDate(state.newGoalEndDate)
                val targetResult = goalValidator.validateTarget(state.targetState.text.toString())
                val nameResult = goalValidator.validateName(state.nameState.text.toString())

                val dateError = (dateResult as? GoalValidationResult.Error)?.message
                val targetError = (targetResult as? GoalValidationResult.Error)?.message
                val nameError = (nameResult as? GoalValidationResult.Error)?.message

                if(dateError != null || targetError != null || nameError != null){
                    state = state.copy(
                        dateError = dateError,
                        targetError = targetError,
                        nameError = nameError
                    )
                    return
                }
                state = state.copy(
                    dateError = null,
                    targetError = null,
                    nameError = null,
                    isSaving = true
                )
                saveGoal()
            }
            GoalAction.HideDatePickerDialog -> {
                state = state.copy(
                    showDatePicker = false
                )
            }
            is GoalAction.OnDateSelected -> {
                state = state.copy(
                    newGoalEndDate = goalAction.date,
                    formattedGoalEndDate = dateFormatter.formatDate(goalAction.date),
                    dateError = null
                )
            }
            GoalAction.ShowDatePickerDialog -> {
                state = state.copy(
                    showDatePicker = true,
                )
            }
        }
    }
    private fun saveGoal(){
        viewModelScope.launch {
            try {
                val targetKm = state.targetState.text.toString().toDoubleOrNull() ?: 0.0
                val targetMeters = (targetKm * 1000).toLong()
                val goal = Goal(
                    id = UUID.randomUUID().toString(),
                    name = state.nameState.text.toString(),
                    targetDistanceMeters = targetMeters,
                    currentDistanceMeters = 0L,
                    endDate = state.newGoalEndDate?.toZonedDateTime() ?: ZonedDateTime.now(),
                )
                goalRepository.upsertGoal(goal)

                state = state.copy(
                    nameState = TextFieldState(),
                    targetState = TextFieldState(),
                    newGoalEndDate = null,
                    formattedGoalEndDate = ""
                )
            }finally {
                state = state.copy(
                    isSaving = false
                )
            }
        }
    }
    private fun deleteGoal(id: String){
        viewModelScope.launch {
            goalRepository.deleteGoal(id)
        }
    }

}