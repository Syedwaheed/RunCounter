package com.edu.run.location

import android.location.Location
import com.edu.core.location.LocationWithAltitude

fun Location.toLocationWithAltitude(): LocationWithAltitude {
    return LocationWithAltitude(
        location = com.edu.core.location.Location(
            lat = latitude,
            long = longitude
        ),
        altitude = altitude
    )
}