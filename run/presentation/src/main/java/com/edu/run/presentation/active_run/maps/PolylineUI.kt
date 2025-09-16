package com.edu.run.presentation.active_run.maps

import androidx.compose.ui.graphics.Color
import com.edu.core.location.Location

data class PolylineUI(
    val location1: Location,
    val location2: Location,
    val color: Color
)
