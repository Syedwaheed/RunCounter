package com.edu.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.core.domain.SessionStorage
import com.edu.core.domain.run.RunRepository
import com.edu.goal.domain.GoalRepository
import com.edu.run.domain.SyncScheduler
import com.edu.run.presentation.run_overview.model.mappers.toRunUi
import com.edu.run.presentation.util.RunStatsCalculator
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class RunOverviewViewModel(
    private val runRepository: RunRepository,
    private val sessionStorage: SessionStorage,
    private val syncScheduler: SyncScheduler,
    private val goalRepository: GoalRepository
): ViewModel() {

    var state by mutableStateOf(RunOverViewState())
        private set

    private val eventChannel = Channel<RunOverViewEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        syncScheduler.scheduleSync(interval = 30.minutes)

        combine(
            runRepository.getRuns(),
            goalRepository.getGoals()
        ) { runs, goals ->
            val goalMap = goals.associate { it.id to it.name }
            runs.map { run ->
                run.toRunUi(goalName = run.goalId?.let { goalMap[it] })
            }
        }
            .onEach { runUIs ->
                state = state.copy(
                    runs = runUIs,
                    totalRuns = runUIs.size,
                    totalDistance = RunStatsCalculator.calculateTotalDistance(runUIs),
                    totalDuration = RunStatsCalculator.calculateTotalDuration(runUIs),
                    thisWeekRuns = RunStatsCalculator.calculateThisWeekRuns(runUIs)
                )
            }
            .launchIn(viewModelScope)
        viewModelScope.launch {
            runRepository.syncPendingRuns()
            runRepository.fetchRuns()
        }
    }

    fun onAction(action: RunOverViewAction){
        when(action){
            RunOverViewAction.OnLogoutClick -> logout()
            RunOverViewAction.OnScreenResume -> refreshRuns()
            is RunOverViewAction.DeleteRun -> {
                viewModelScope.launch {
                    runRepository.deleteRun(action.runUi.id)
                }
            }
            else -> Unit
        }
    }

    private fun refreshRuns() {
        viewModelScope.launch {
            runRepository.fetchRuns()
        }
    }

    private fun logout() {
        viewModelScope.launch {
            syncScheduler.cancelSync()
            // Clear local runs cache (server data intact, will be fetched on next login)
            // Goals don't need clearing - they're filtered by userId
            runRepository.deleteAllRuns()
            sessionStorage.set(null)
            eventChannel.send(RunOverViewEvent.LogoutSuccess)
        }
    }
}