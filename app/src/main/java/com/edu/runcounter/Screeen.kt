package com.edu.runcounter

import kotlinx.serialization.Serializable
import okhttp3.Route


sealed class Screen{
    @Serializable
    data object AuthGraph : Screen()
}

sealed class Dest{
    @Serializable
    data object AuthIntroScreen: Dest()
    @Serializable
    data object AuthRegistrationScreen: Dest()
    @Serializable
    data object AuthLoginScreen: Dest()
}