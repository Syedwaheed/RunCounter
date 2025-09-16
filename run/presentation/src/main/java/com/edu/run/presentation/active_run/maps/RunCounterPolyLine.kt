package com.edu.run.presentation.active_run.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.edu.core.location.LocationTimeStamp
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Polyline

@Composable
fun RunCounterPolyLine(
    modifier: Modifier = Modifier,
    locations: List<List<LocationTimeStamp>>
) {
    val polylines = remember(locations){
        locations.map {
            it.zipWithNext { timestamps1, timestamps2 ->
                PolylineUI(
                    location1 = timestamps1.location.location,
                    location2 = timestamps2.location.location,
                    color = PolylineColorCalculator.locationToColor(
                        location1 = timestamps1,
                        location2 = timestamps2
                    )
                )
            }
        }
    }
    polylines.forEach { polyline ->
        polyline.forEach { polylineUI ->
            Polyline(
                points = listOf(
                    LatLng(polylineUI.location1.lat,polylineUI.location1.long),
                    LatLng(polylineUI.location2.lat,polylineUI.location2.long)
                ),
                color = polylineUI.color,
                jointType = JointType.BEVEL
            )
        }
    }
}