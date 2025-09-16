@file:OptIn(ExperimentalCoroutinesApi::class)

package com.edu.run.domain

import com.edu.core.domain.Timer
import com.edu.core.location.LocationTimeStamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class RunningTracker(
    private val locationObserver: LocationObserver,
    private val applicationScope: CoroutineScope
) {

    private val _runData = MutableStateFlow(RunData())
    val runData = _runData.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()
    private val isObservingLocation = MutableStateFlow(false)

    private val _elapsedTime = MutableStateFlow(Duration.ZERO)
    val elapsedTime = _elapsedTime.asStateFlow()

    val currentLocation =  isObservingLocation
        .flatMapLatest { isObservingLocation ->
            if(isObservingLocation){
                locationObserver.observeLocation(1000L)
            }else flowOf()
        }
        .stateIn(
            applicationScope,
            SharingStarted.Lazily,
            null
        )
    init {
        _isTracking
            .onEach { isTracking ->
                if(!isTracking){
                    val newList = buildList {
                        addAll(runData.value.location)
                        add(emptyList<LocationTimeStamp>())
                    }.toList()
                    _runData.update {
                        it.copy(
                            location = newList
                        )
                    }
                }

            }
            .flatMapLatest {  isTracking ->
                if(isTracking){
                    Timer.timeAndEmit()
                }else flowOf()
            }
            .onEach {
                _elapsedTime.value += it
            }
            .launchIn(applicationScope)

        currentLocation
            .filterNotNull()
            .combineTransform(_isTracking){ location, isTracking ->
                if(isTracking){
                    emit(location)
                }
            }
            .zip(_elapsedTime){ location, elapsedTime ->
                LocationTimeStamp(
                    location = location,
                    durationTimeStamp = elapsedTime
                )
            }
            .onEach { location ->
                val currentLocation = runData.value.location
                val lastLocationList = if(currentLocation.isNotEmpty()){
                    currentLocation.last() + location
                }else{
                    listOf(location)
                }
                val newLocationList =  currentLocation.replaceLast(lastLocationList)

                val distanceMeters = LocationDataCalculator.getTotalDistanceMeters(
                    locations = newLocationList
                )
                val distanceKilometers = distanceMeters /1000.0
                val currentDuration = location.durationTimeStamp

                val avgSecondsPerKm = if(distanceKilometers == 0.0){
                    0
                } else {
                    (currentDuration.inWholeSeconds / distanceKilometers).roundToInt()
                }
                _runData.update {
                    RunData(
                        distanceMeters = distanceMeters,
                        pace = avgSecondsPerKm.seconds,
                        location = newLocationList,
                    )
                }
            }
            .launchIn(applicationScope)


    }
    fun setIsTracking(isTracking: Boolean){
        this._isTracking.value = isTracking
    }


    fun startObservingLocation(){
        isObservingLocation.value = true
    }
    fun stopObservingLocation(){
        isObservingLocation.value = false
    }
}
private fun <T> List<List<T>>.replaceLast(replacement: List<T>): List<List<T>>{
    if(this.isEmpty()){
        return listOf(replacement)
    }
    return this.dropLast(1) + listOf(replacement)
}