package com.amy.daily5vocab.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amy.daily5vocab.data.auth.AuthRepository
import com.amy.daily5vocab.ui.auth.AuthViewModel
import com.amy.daily5vocab.ui.auth.LoginScreen
import com.amy.daily5vocab.ui.auth.RegisterScreen
import com.amy.daily5vocab.ui.onboarding.OnboardingScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val ONBOARDING = "onboarding"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Skip auth if a user session already exists.
    val startDestination = if (AuthRepository().currentUser != null) {
        Routes.ONBOARDING
    } else {
        Routes.LOGIN
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            val viewModel: AuthViewModel = viewModel()
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onLogin = { email, password ->
                    viewModel.login(email, password) {
                        navController.navigate(Routes.ONBOARDING) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                },
                isLoading = viewModel.uiState.isLoading,
                errorMessage = viewModel.uiState.errorMessage,
            )
        }
        composable(Routes.REGISTER) {
            val viewModel: AuthViewModel = viewModel()
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegister = { name, email, password ->
                    viewModel.register(name, email, password) {
                        navController.navigate(Routes.ONBOARDING) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                },
                isLoading = viewModel.uiState.isLoading,
                errorMessage = viewModel.uiState.errorMessage,
            )
        }
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                // No navigation wired yet, as requested.
                onStartLearning = { /* TODO: persist topics + go to the daily screen */ },
            )
        }
    }
}
