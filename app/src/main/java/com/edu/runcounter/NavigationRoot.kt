package com.edu.runcounter

import LoginRoot
import android.net.http.SslCertificate.saveState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.edu.auth.presentation.intro.IntroScreenRoot
import com.edu.auth.presentation.register.RegisterScreenRoot
import com.edu.run.presentation.active_run.ActiveRunScreenRoot
import com.edu.run.presentation.active_run.service.ActiveRunService
import com.edu.run.presentation.run_overview.RunOverviewScreen
import com.edu.run.presentation.run_overview.RunOverviewScreenRoot


@Composable
fun NavigationRoot(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    isLoggedIn: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Screen.DashboardGraph else Screen.AuthGraph
    ) {
        authGraph(navController)
        runGraph(navController)
    }
}

private fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation<Screen.AuthGraph>(
        startDestination = AuthDest.AuthIntroScreen
    ) {
        composable<AuthDest.AuthIntroScreen> {
            IntroScreenRoot(
                onSignInClick = { navController.navigate(AuthDest.AuthLoginScreen) },
                onSignUpClick = { navController.navigate(AuthDest.AuthRegistrationScreen) }
            )
        }
        composable<AuthDest.AuthRegistrationScreen> {
            RegisterScreenRoot(
                onSignInClick = {
                    navController.navigate(AuthDest.AuthLoginScreen) {
                        popUpTo(AuthDest.AuthRegistrationScreen) {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                onSuccessfulRegistration = { navController.navigate(AuthDest.AuthLoginScreen) }
            )
        }
        composable<AuthDest.AuthLoginScreen> {
            LoginRoot(
                onLoginSuccess = {
                    navController.navigate(DashboardDest.DashboardScreen) {
                        popUpTo(Screen.AuthGraph) {
                            inclusive = true
                        }
                    }
                },
                onSignUpClick = {
                    navController.navigate(AuthDest.AuthRegistrationScreen) {
                        popUpTo(AuthDest.AuthLoginScreen) {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                }
            )
        }

    }

}

private fun NavGraphBuilder.runGraph(navController: NavHostController) {
    navigation<Screen.DashboardGraph>(
        startDestination = DashboardDest.DashboardScreen
    ) {
        composable<DashboardDest.DashboardScreen> {
            RunOverviewScreenRoot(
                onStartRunClick = {
                    navController.navigate(DashboardDest.ActiveRunScreen)
                }
            )
        }
        composable<DashboardDest.ActiveRunScreen>(
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "runcounter://active_run"
                }
            )
        ) {
            val context = LocalContext.current
            ActiveRunScreenRoot(
                onServiceToggle = { shouldServiceRun ->
                    if (shouldServiceRun) {
                        context.startService(
                            ActiveRunService.createStartIntent(
                                context = context,
                                activityClass = MainActivity::class.java
                            )
                        )
                    }else{
                        context.startService(
                            ActiveRunService.createStopIntent(
                                context = context
                            )
                        )
                    }
                }
            )
        }
    }
}