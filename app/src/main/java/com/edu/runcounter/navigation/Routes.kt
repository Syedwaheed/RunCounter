package com.edu.runcounter.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Navigation routes for the app using Navigation 3.
 * All routes implement NavKey for type-safe navigation and back stack persistence.
 */

// Auth Flow Routes
@Serializable
data object IntroRoute : NavKey

@Serializable
data object RegisterRoute : NavKey

@Serializable
data object LoginRoute : NavKey

// Dashboard Flow Routes
@Serializable
data object DashboardRoute : NavKey

@Serializable
data class ActiveRunRoute(val sessionId: Long = System.currentTimeMillis()) : NavKey

@Serializable
data object SettingsRoute : NavKey

@Serializable
data class RunDetailRoute(val runId: String) : NavKey