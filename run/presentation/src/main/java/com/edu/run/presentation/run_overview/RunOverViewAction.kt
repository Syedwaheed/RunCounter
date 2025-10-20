package com.edu.run.presentation.run_overview

import com.edu.run.presentation.run_overview.model.RunUI

sealed interface RunOverViewAction {
    data object OnStartClick: RunOverViewAction
    data object OnLogoutClick: RunOverViewAction
    data object OnAnalyticClick: RunOverViewAction
    data class DeleteRun(val runUi: RunUI): RunOverViewAction
}