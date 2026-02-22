package com.edu.runcounter.navigation

import LoginRoot
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.edu.auth.presentation.intro.IntroScreenRoot
import com.edu.auth.presentation.register.RegisterScreenRoot
import com.edu.run.presentation.active_run.ActiveRunScreenRoot
import com.edu.run.presentation.active_run.service.ActiveRunService
import com.edu.run.presentation.settings.SettingsRoot
import com.edu.runcounter.MainActivity
import com.edu.runcounter.dashboard.MainDashBoardScreen

/**
 * Main navigation composable using Navigation 3.
 * Uses a state-driven back stack approach instead of route-based navigation.
 */
@Composable
fun AppNavigation(
    isLoggedIn: Boolean,
    modifier: Modifier = Modifier
) {
    // Create a saveable back stack that persists across configuration changes
    val startRoute: NavKey = if (isLoggedIn) DashboardRoute else IntroRoute
    val backStack = rememberNavBackStack(startRoute)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        modifier = modifier,
        entryProvider = entryProvider {
            // Auth Flow
            entry<IntroRoute> {
                IntroScreenRoot(
                    onSignInClick = {
                        backStack.add(LoginRoute)
                    },
                    onSignUpClick = {
                        backStack.add(RegisterRoute)
                    }
                )
            }

            entry<RegisterRoute> {
                RegisterScreenRoot(
                    onSignInClick = {
                        // Replace current with login
                        backStack.removeLastOrNull()
                        backStack.add(LoginRoute)
                    },
                    onSuccessfulRegistration = {
                        backStack.removeLastOrNull()
                        backStack.add(LoginRoute)
                    }
                )
            }

            entry<LoginRoute> {
                LoginRoot(
                    onLoginSuccess = {
                        // Clear auth stack and go to dashboard
                        backStack.clear()
                        backStack.add(DashboardRoute)
                    },
                    onSignUpClick = {
                        backStack.removeLastOrNull()
                        backStack.add(RegisterRoute)
                    }
                )
            }

            // Dashboard Flow
            entry<DashboardRoute> {
                MainDashBoardScreen(
                    onStartRunClick = {
                        backStack.add(ActiveRunRoute)
                    },
                    onLogOutClick = {
                        // Clear stack and go back to intro
                        backStack.clear()
                        backStack.add(IntroRoute)
                    },
                    onSettingsClick = {
                        backStack.add(SettingsRoute)
                    }
                )
            }

            entry<ActiveRunRoute> {
                val context = LocalContext.current
                ActiveRunScreenRoot(
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    onFinish = {
                        backStack.removeLastOrNull()
                    },
                    onServiceToggle = { shouldServiceRun ->
                        if (shouldServiceRun) {
                            context.startService(
                                ActiveRunService.createStartIntent(
                                    context = context,
                                    activityClass = MainActivity::class.java
                                )
                            )
                        } else {
                            context.startService(
                                ActiveRunService.createStopIntent(
                                    context = context
                                )
                            )
                        }
                    }
                )
            }

            entry<SettingsRoute> {
                SettingsRoot(
                    onBackClick = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}