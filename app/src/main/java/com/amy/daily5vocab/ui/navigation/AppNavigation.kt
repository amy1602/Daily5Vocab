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
import com.amy.daily5vocab.ui.common.AppTab
import com.amy.daily5vocab.ui.onboarding.OnboardingScreen
import com.amy.daily5vocab.ui.settings.SettingsScreen
import com.amy.daily5vocab.ui.today.TodayScreen
import androidx.navigation.NavController

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val ONBOARDING = "onboarding"
    const val TODAY = "today"
    const val SETTINGS = "settings"
}

/** Switches between the bottom-nav tab destinations without stacking duplicates. */
private fun NavController.selectTab(tab: AppTab) {
    val route = when (tab) {
        AppTab.Today -> Routes.TODAY
        AppTab.Settings -> Routes.SETTINGS
        AppTab.History -> return // History screen not built yet.
    }
    if (route == currentDestination?.route) return
    navigate(route) {
        popUpTo(Routes.TODAY)
        launchSingleTop = true
    }
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
                onStartLearning = {
                    // TODO: persist selected topics.
                    navController.navigate(Routes.TODAY) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.TODAY) {
            TodayScreen(
                selectedTab = AppTab.Today,
                onTabSelected = { navController.selectTab(it) },
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                selectedTab = AppTab.Settings,
                onTabSelected = { navController.selectTab(it) },
                onSignOut = {
                    AuthRepository().logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
    }
}
