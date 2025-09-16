package com.edu.run.domain

import com.edu.core.location.Location
import com.edu.core.location.LocationTimeStamp
import kotlin.time.Duration

data class RunData(
    val distanceMeters: Int = 0,
    val pace: Duration = Duration.ZERO,
    val location: List<List<LocationTimeStamp>> = emptyList()
)
