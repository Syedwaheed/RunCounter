package com.edu.run.presentation.analytics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.core.domain.run.RunRepository
import com.edu.core.presentation.ui.formatted
import com.edu.core.presentation.ui.toFormattedKm
import com.edu.core.presentation.ui.toFormattedKmh
import com.edu.core.presentation.ui.toFormattedPace
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration

class AnalyticsViewModel(
    private val runRepository: RunRepository
) : ViewModel() {

    var state by mutableStateOf(AnalyticsState())
        private set

    init {
        runRepository.getRuns()
            .onEach { runs ->
                val totalDistanceKm = runs.sumOf { it.distanceMeters } / 1000.0
                val totalDuration = runs.fold(Duration.ZERO) { acc, run -> acc + run.duration }
                val maxSpeedKmh = runs.maxOfOrNull { it.maxSpeedKmh } ?: 0.0
                val avgDistanceKm = if (runs.isNotEmpty()) totalDistanceKm / runs.size else 0.0

                state = state.copy(
                    totalDistanceRun = totalDistanceKm.toFormattedKm(),
                    totalTimeRun = totalDuration.formatted(),
                    fastestEverRun = maxSpeedKmh.toFormattedKmh(),
                    avgDistance = avgDistanceKm.toFormattedKm(),
                    avgPace = totalDuration.toFormattedPace(totalDistanceKm),
                    totalRuns = runs.size
                )
            }
            .launchIn(viewModelScope)
    }
}