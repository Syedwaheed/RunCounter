package com.edu.run.presentation.run_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.core.domain.run.RunRepository
import com.edu.goal.domain.GoalRepository
import com.edu.run.presentation.run_overview.model.mappers.toRunUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class RunDetailViewModel(
    private val runId: String,
    private val runRepository: RunRepository,
    private val goalRepository: GoalRepository
) : ViewModel() {

    var state by mutableStateOf(RunDetailState())
        private set

    private val eventChannel = Channel<RunDetailEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        Timber.d("RunDetailViewModel initialized with runId: $runId")
        loadRun()
    }

    private fun loadRun() {
        // Use reactive Flow collection like RunOverviewViewModel
        combine(
            runRepository.getRuns(),
            goalRepository.getGoals()
        ) { runs, goals ->
            Timber.d("RunDetailViewModel: Found ${runs.size} runs, looking for id: $runId")
            val goalMap = goals.associate { it.id to it.name }
            val run = runs.find { it.id == runId }
            Timber.d("RunDetailViewModel: Found run: ${run != null}, run.id: ${run?.id}")
            run?.toRunUi(goalName = run.goalId?.let { goalMap[it] })
        }
            .onEach { runUI ->
                state = state.copy(
                    run = runUI,
                    isLoading = false
                )
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: RunDetailAction) {
        when (action) {
            RunDetailAction.OnBackClick -> {
                viewModelScope.launch {
                    eventChannel.send(RunDetailEvent.NavigateBack)
                }
            }
            RunDetailAction.OnDeleteClick -> {
                viewModelScope.launch {
                    runRepository.deleteRun(runId)
                    eventChannel.send(RunDetailEvent.RunDeleted)
                }
            }
        }
    }
}

sealed interface RunDetailEvent {
    data object NavigateBack : RunDetailEvent
    data object RunDeleted : RunDetailEvent
}
