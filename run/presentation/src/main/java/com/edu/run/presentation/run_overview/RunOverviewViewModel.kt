package com.edu.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.core.domain.run.RunRepository
import com.edu.run.presentation.run_overview.model.mappers.toRunUi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class RunOverviewViewModel(
    private val runRepository: RunRepository
): ViewModel() {

    var state by mutableStateOf(RunOverViewState())
        private set

    init {
        runRepository.getRuns()
            .onEach { runs ->
                val runUI = runs.map { it.toRunUi() }
                state = state.copy( runs = runUI )
            }
             .launchIn(viewModelScope)
        viewModelScope.launch {
            runRepository.fetchRuns()
        }
    }

    fun onAction(action: RunOverViewAction){
        when(action){
            RunOverViewAction.OnLogoutClick -> Unit
            RunOverViewAction.OnStartClick -> Unit
            is RunOverViewAction.DeleteRun -> {
                viewModelScope.launch {
                    runRepository.deleteRun(action.runUi.id)
                }
            }
            else -> Unit
        }
    }
}