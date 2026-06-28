package com.amy.daily5vocab.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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
import com.amy.daily5vocab.ui.settings.SettingsViewModel
import com.amy.daily5vocab.ui.theme.GrowthGreenDeep
import com.amy.daily5vocab.ui.today.TodayScreen
import com.amy.daily5vocab.ui.today.TodayViewModel
import com.amy.daily5vocab.ui.topic.ChangeTopicScreen
import com.amy.daily5vocab.ui.topic.ChangeTopicViewModel

object Routes {
    const val LAUNCH = "launch"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val ONBOARDING = "onboarding"
    const val TODAY = "today"
    const val SETTINGS = "settings"
    const val CHANGE_TOPIC = "change_topic"
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
            // Refresh when this destination resumes so topic edits made in Settings
            // (which can change the default topic) are reflected on return.
            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) viewModel.load()
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
            }
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
            val viewModel: SettingsViewModel = viewModel()
            SettingsScreen(
                reminderTime = viewModel.uiState.reminderTime,
                onReminderTimeSelected = { viewModel.setReminderTime(it) },
                selectedTab = AppTab.Settings,
                onTabSelected = { navController.selectTab(it) },
                onChangeTopics = { navController.navigate(Routes.CHANGE_TOPIC) },
                onSignOut = {
                    AuthRepository().logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.CHANGE_TOPIC) {
            val viewModel: ChangeTopicViewModel = viewModel()
            val state = viewModel.uiState
            ChangeTopicScreen(
                selected = state.selected,
                onToggle = { viewModel.toggle(it) },
                onSave = { viewModel.save { navController.popBackStack() } },
                onBack = { navController.popBackStack() },
                canSave = state.hasChanges,
                isSaving = state.isSaving,
                errorMessage = state.errorMessage,
                selectedTab = AppTab.Settings,
                onTabSelected = { navController.selectTab(it) },
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
