package com.edu.runcounter

import android.net.http.SslCertificate.restoreState
import android.net.http.SslCertificate.saveState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.edu.auth.presentation.intro.IntroScreenRoot
import com.edu.auth.presentation.register.RegisterScreen
import com.edu.auth.presentation.register.RegisterScreenRoot
import io.ktor.http.auth.AuthScheme
import okhttp3.Route


@Composable
fun NavigationRoot(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.AuthGraph
    ) {
      authGraph(navController)
    }
}

private fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation<Screen.AuthGraph>(
        startDestination = Dest.AuthIntroScreen
    ) {
        composable<Dest.AuthIntroScreen> {
            IntroScreenRoot(
                onSignInClick = { navController.navigate(Dest.AuthLoginScreen) },
                onSignUpClick = { navController.navigate(Dest.AuthRegistrationScreen) }
            )
        }
        composable<Dest.AuthRegistrationScreen> {
            RegisterScreenRoot(
                onSignInClick = {
                    navController.navigate(Dest.AuthLoginScreen) {
                        popUpTo(Dest.AuthRegistrationScreen) {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                onSuccessfulRegistration = { navController.navigate(Dest.AuthLoginScreen) }
            )
        }
        composable<Dest.AuthLoginScreen> {
            Text(
                text = "Login"
            )
        }

    }
}