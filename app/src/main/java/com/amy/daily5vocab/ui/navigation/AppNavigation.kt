package com.amy.daily5vocab.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amy.daily5vocab.data.auth.AuthRepository
import com.amy.daily5vocab.data.user.UserRepository
import com.amy.daily5vocab.ui.auth.AuthViewModel
import com.amy.daily5vocab.ui.auth.LoginScreen
import com.amy.daily5vocab.ui.auth.RegisterScreen
import com.amy.daily5vocab.ui.common.AppTab
import com.amy.daily5vocab.ui.onboarding.OnboardingScreen
import com.amy.daily5vocab.ui.onboarding.OnboardingViewModel
import com.amy.daily5vocab.ui.settings.SettingsScreen
import com.amy.daily5vocab.ui.theme.GrowthGreenDeep
import com.amy.daily5vocab.ui.today.TodayScreen
import com.amy.daily5vocab.ui.today.TodayViewModel

object Routes {
    const val LAUNCH = "launch"
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

    NavHost(navController = navController, startDestination = Routes.LAUNCH) {
        // Decides where to send the user: auth -> onboarding (no topics yet) -> today.
        composable(Routes.LAUNCH) {
            LaunchGate(
                onRoute = { route ->
                    navController.navigate(route) {
                        popUpTo(Routes.LAUNCH) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.LOGIN) {
            val viewModel: AuthViewModel = viewModel()
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onLogin = { email, password ->
                    viewModel.login(email, password) {
                        // Returning users may already have topics; let the gate decide.
                        navController.navigate(Routes.LAUNCH) {
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
                        // New accounts have no topics yet -> the gate routes to onboarding.
                        navController.navigate(Routes.LAUNCH) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                },
                isLoading = viewModel.uiState.isLoading,
                errorMessage = viewModel.uiState.errorMessage,
            )
        }
        composable(Routes.ONBOARDING) {
            val viewModel: OnboardingViewModel = viewModel()
            OnboardingScreen(
                onStartLearning = { selected ->
                    viewModel.saveTopics(selected) {
                        navController.navigate(Routes.TODAY) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    }
                },
                isSaving = viewModel.uiState.isSaving,
                errorMessage = viewModel.uiState.errorMessage,
            )
        }
        composable(Routes.TODAY) {
            val viewModel: TodayViewModel = viewModel()
            val state = viewModel.uiState
            TodayScreen(
                topic = state.topic,
                words = state.words,
                isLoading = state.isLoading,
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

/**
 * Loading gate that resolves the right start destination once:
 *  - not signed in           -> Login
 *  - signed in, no topics yet -> Onboarding (shown only this once)
 *  - signed in, has topics    -> Today
 */
@Composable
private fun LaunchGate(onRoute: (String) -> Unit) {
    LaunchedEffect(Unit) {
        if (AuthRepository().currentUser == null) {
            onRoute(Routes.LOGIN)
            return@LaunchedEffect
        }
        UserRepository().getTopics { result ->
            val hasTopics = result.getOrNull()?.isNotEmpty() == true
            onRoute(if (hasTopics) Routes.TODAY else Routes.ONBOARDING)
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = GrowthGreenDeep)
    }
}
