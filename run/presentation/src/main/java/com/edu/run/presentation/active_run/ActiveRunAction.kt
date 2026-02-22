package com.edu.run.presentation.active_run

import com.edu.goal.domain.Goal

sealed interface ActiveRunAction{
    data object OnToggleRunClick: ActiveRunAction
    data object OnFinishRunClick : ActiveRunAction
    data object OnResumeRunClick : ActiveRunAction
    data object OnBackClick : ActiveRunAction
    data class SubmitLocationPermissionInfo(
        val acceptedLocationPermission: Boolean,
        val showLocationRationale: Boolean
    ): ActiveRunAction

    data class SubmitNotificationInfo(
        val acceptedNotificationPermission: Boolean,
        val showNotificationRationale: Boolean
    ): ActiveRunAction
   data object DismissRationaleDialog: ActiveRunAction
    class OnRunProcess(val mapPictureBytes: ByteArray): ActiveRunAction

    data class OnSelectGoal(val goal: Goal?): ActiveRunAction
    data object OnShowGoalSelection: ActiveRunAction
    data object OnDismissGoalSelection: ActiveRunAction
}