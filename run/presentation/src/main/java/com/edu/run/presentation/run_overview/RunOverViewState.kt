package com.edu.run.presentation.run_overview

import com.edu.run.presentation.run_overview.model.RunUI

data class RunOverViewState(
    val runs: List<RunUI> = emptyList()
)
