package com.edu.run.presentation.run_overview.model.mappers

import com.edu.core.domain.run.Run
import com.edu.core.presentation.ui.formatted
import com.edu.core.presentation.ui.toFormattedKm
import com.edu.core.presentation.ui.toFormattedKmh
import com.edu.core.presentation.ui.toFormattedMeters
import com.edu.core.presentation.ui.toFormattedPace
import com.edu.run.presentation.run_overview.model.RunUI
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Run.toRunUi(goalName: String? = null): RunUI{
    val dateTimeInLocalTime = dateTimeUtc
        .withZoneSameInstant(ZoneId.systemDefault())
    val formattedDateTime = DateTimeFormatter
        .ofPattern("MMM dd, yyyy - hh:mma")
        .format(dateTimeInLocalTime)

    val distanceKm = distanceMeters / 1000.0

    return RunUI(
        id = id!!,
        duration = duration.formatted(),
        dateTime = formattedDateTime,
        dateTimeEpoch = dateTimeUtc.toInstant().toEpochMilli(),
        distance = distanceKm.toFormattedKm(),
        avgSpeed = averageSpeedKmh.toFormattedKmh(),
        maxSpeed = maxSpeedKmh.toFormattedKmh(),
        pace = duration.toFormattedPace(distanceKm),
        totalElevation = totalElevationMeters.toFormattedMeters(),
        mapPictureUrl = mapPictureUrl,
        goalId = goalId,
        goalName = goalName
    )
}