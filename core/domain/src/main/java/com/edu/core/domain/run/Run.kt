package com.edu.core.domain.run

import com.edu.core.location.Location
import java.time.ZonedDateTime
import kotlin.time.Duration
import kotlin.time.DurationUnit

data class Run(
    val id: String?, //null if new run
    val duration: Duration,
    val dateTimeUtc: ZonedDateTime,
    val distanceMeters: Int,
    val location: Location,
    val maxSpeedKmh: Double,
    val totalElevationMeters: Int,
    val mapPictureUrl: String?,
    val goalId: String? = null
){
    val averageSpeedKmh: Double
        get() {
            val hours = duration.toDouble(DurationUnit.HOURS)
            return if (hours == 0.0) 0.0 else (distanceMeters / 1000.0) / hours
        }
}
