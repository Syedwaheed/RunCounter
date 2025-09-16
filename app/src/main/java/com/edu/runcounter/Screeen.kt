package com.edu.runcounter

import kotlinx.serialization.Serializable


sealed class Screen{
    @Serializable
    data object AuthGraph : Screen()
    @Serializable
    data object DashboardGraph : Screen()
}

sealed class AuthDest{
    @Serializable
    data object AuthIntroScreen: AuthDest()
    @Serializable
    data object AuthRegistrationScreen: AuthDest()
    @Serializable
    data object AuthLoginScreen: AuthDest()
}
sealed class DashboardDest{
    @Serializable
    data object DashboardScreen: DashboardDest()
    @Serializable
    data object ActiveRunScreen: DashboardDest()
}