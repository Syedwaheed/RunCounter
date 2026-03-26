package com.edu.run.presentation.util

import com.edu.run.presentation.run_overview.model.RunUI
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters

object RunStatsCalculator {

    fun calculateTotalDistance(runs: List<RunUI>): String {
        if (runs.isEmpty()) return "0 km"
        val totalKm = runs.sumOf { run ->
            run.distance
                .replace(Regex("[^0-9.]"), "")
                .toDoubleOrNull() ?: 0.0
        }
        return "%.1f km".format(totalKm)
    }

    fun calculateTotalDuration(runs: List<RunUI>): String {
        if (runs.isEmpty()) return "0m"
        var totalSeconds = 0L
        runs.forEach { run ->
            val parts = run.duration.split(":")
            if (parts.size == 3) {
                totalSeconds += (parts[0].toLongOrNull() ?: 0L) * 3600
                totalSeconds += (parts[1].toLongOrNull() ?: 0L) * 60
                totalSeconds += parts[2].toLongOrNull() ?: 0L
            }
        }
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
    }

    fun calculateThisWeekRuns(runs: List<RunUI>): Int {
        if (runs.isEmpty()) return 0
        val now = ZonedDateTime.now(ZoneId.systemDefault())
        val startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            .toLocalDate().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return runs.count { it.dateTimeEpoch in startOfWeek until endOfWeek }
    }
}
