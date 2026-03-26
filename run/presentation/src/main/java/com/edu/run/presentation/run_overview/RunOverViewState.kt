package com.edu.run.presentation.run_overview

import com.edu.run.presentation.run_overview.model.RunUI

data class RunOverViewState(
    val runs: List<RunUI> = emptyList(),
    val totalRuns: Int = 0,
    val totalDistance: String = "0 km",
    val totalDuration: String = "0m",
    val thisWeekRuns: Int = 0
)
