package com.edu.core.presentation.designsystem

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val DarkColorScheme = darkColorScheme(
    primary = RunCounterGreen,
    background = RunCounterBlack,
    surface = RunCounterDarkGray,
    secondary = RunCounterWhite,
    tertiary = RunCounterWhite,
    primaryContainer = RunCounterGreen30,
    onPrimary = RunCounterBlack,
    onBackground = RunCounterWhite,
    onSurface = RunCounterWhite,
    onSurfaceVariant = RunCounterGray,
    error = RunCounterDarkRed
)
@Composable
fun RunCounterTheme(
    content: @Composable () -> Unit
){
    val view = LocalView.current
    if(!view.isInEditMode){
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window,view).isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content

    )
}