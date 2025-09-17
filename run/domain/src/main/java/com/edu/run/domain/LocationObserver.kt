package com.edu.run.domain

import com.edu.core.location.LocationWithAltitude
import kotlinx.coroutines.flow.Flow

interface LocationObserver {
    //comment Added
    fun observeLocation(interval: Long): Flow<LocationWithAltitude>
}