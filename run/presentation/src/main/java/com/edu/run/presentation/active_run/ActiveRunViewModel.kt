package com.edu.run.presentation.active_run

import android.system.Os.stat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edu.core.domain.run.Run
import com.edu.core.domain.run.RunRepository
import com.edu.core.domain.util.Result
import com.edu.core.location.Location
import com.edu.core.presentation.ui.asUiText
import com.edu.goal.domain.GoalRepository
import com.edu.run.domain.LocationDataCalculator
import com.edu.run.domain.RunningTracker
import com.edu.run.presentation.active_run.service.ActiveRunService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.ZoneId
import java.time.ZonedDateTime

class ActiveRunViewModel(
    private val runningTracker: RunningTracker,
    private val runRepository: RunRepository,
    private val goalRepository: GoalRepository
) : ViewModel(){
    var state by mutableStateOf(ActiveRunState(
        shouldTrack = ActiveRunService.isServiceActive && runningTracker.isTracking.value,
        hasStartedRunning = ActiveRunService.isServiceActive
    ))
        private set

    private val _eventChannel = Channel<ActiveRunEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    private val shouldTrack = snapshotFlow { state.shouldTrack }
        .stateIn(viewModelScope, SharingStarted.Lazily,state.shouldTrack)

    private val hasLocationPermission = MutableStateFlow(false)

    private val isTracking = combine(
        shouldTrack,
        hasLocationPermission
    ){ shouldTrack, hasLocationPermission ->
        shouldTrack && hasLocationPermission
    }.stateIn(viewModelScope, SharingStarted.Lazily,false)
    init {
        hasLocationPermission
            .onEach { hasPermission ->
                if(hasPermission){
                    runningTracker.startObservingLocation()
                }else{
                    runningTracker.stopObservingLocation()
                }
            }
            .launchIn(viewModelScope)

        isTracking
            .onEach { isTracking ->
                runningTracker.setIsTracking(isTracking)
            }
            .launchIn(viewModelScope)
        runningTracker
            .currentLocation
            .onEach {
                state = state.copy(
                    currentLocation = it?.location
                )
            }
            .launchIn(viewModelScope)
        runningTracker
            .runData
            .onEach {
                if(!state.isFinished){
                    state = state.copy( runData = it )
                }
            }
            .launchIn(viewModelScope)

        runningTracker
            .elapsedTime
            .onEach {
                state = state.copy(
                    elapsedTime = it
                )
            }
            .launchIn(viewModelScope)

        goalRepository
            .getAvailableGoals()
            .onEach { goals ->
                state = state.copy(availableGoals = goals)
            }
            .launchIn(viewModelScope)
    }
    fun onAction(action: ActiveRunAction){
        when(action){
            ActiveRunAction.OnBackClick -> {
                state = state.copy(
                    shouldTrack = false
                )
            }
            ActiveRunAction.OnFinishRunClick -> {
                state = state.copy(
                    isFinished = true,
                    isSavingRun = true,
                )
            }
            ActiveRunAction.OnResumeRunClick -> {
                state = state.copy(
                    shouldTrack = true
                )
            }
            ActiveRunAction.OnToggleRunClick -> {
                state = state.copy(
                    hasStartedRunning = true,
                    shouldTrack = !state.shouldTrack
                )
            }
            is ActiveRunAction.SubmitLocationPermissionInfo -> {
                hasLocationPermission.value = action.acceptedLocationPermission
                state = state.copy(
                    showLocationRationale = action.showLocationRationale
                )
            }
            is ActiveRunAction.SubmitNotificationInfo -> {
                state = state.copy(
                    showNotificationRationale = action.showNotificationRationale,
                    hasNotificationPermission = action.acceptedNotificationPermission
                )
            }
            ActiveRunAction.DismissRationaleDialog -> {
                state = state.copy(
                    showNotificationRationale = false,
                    showLocationRationale = false,
                )
            }
            is ActiveRunAction.OnRunProcess -> {
                finishRun(action.mapPictureBytes)
            }
            is ActiveRunAction.OnSelectGoal -> {
                state = state.copy(
                    selectedGoal = action.goal,
                    showGoalSelectionDialog = false
                )
            }
            ActiveRunAction.OnShowGoalSelection -> {
                state = state.copy(showGoalSelectionDialog = true)
            }
            ActiveRunAction.OnDismissGoalSelection -> {
                state = state.copy(showGoalSelectionDialog = false)
            }
        }
    }

    private fun finishRun(mapPictureBytes: ByteArray) {
        val location = state.runData.location
        Timber.d("location: $location")
        Timber.d("distance ${state.runData.distanceMeters}")
        if(location.isEmpty() || state.runData.distanceMeters<15 ){
            viewModelScope.launch {
                _eventChannel.send(ActiveRunEvent.InsufficientMovement)
            }
            runningTracker.resetRunData()
            state = state.copy(
                isSavingRun = false,
                isFinished = false
            )
            return
        }
        viewModelScope.launch {
            val run = Run(
                id = null,
                duration = state.elapsedTime,
                dateTimeUtc = ZonedDateTime.now()
                    .withZoneSameInstant(ZoneId.of("UTC")),
                distanceMeters = state.runData.distanceMeters,
                location = state.currentLocation ?: Location(0.0,0.0),
                maxSpeedKmh = LocationDataCalculator.getMaxSpeedKmh(location),
                totalElevationMeters = LocationDataCalculator.getTotalElevationMeters(location),
                mapPictureUrl = null,
                goalId = state.selectedGoal?.id
            )
            runningTracker.finishRun()
            Timber.d("run: $run")
            when(val result = runRepository.upsertRun(run,mapPictureBytes)){
                is Result.Error -> {
                    _eventChannel.send(ActiveRunEvent.Error(result.error.asUiText()))
                }
                is Result.Success -> {
                    ActiveRunService.isServiceActive = false
                    _eventChannel.send(ActiveRunEvent.RunSaved)
                }
            }
            state = state.copy(isSavingRun = false)
        }
    }

    override fun onCleared() {
        super.onCleared()
        if(!ActiveRunService.isServiceActive){
            runningTracker.stopObservingLocation()
        }
    }
}