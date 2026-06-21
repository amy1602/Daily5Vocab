package com.amy.daily5vocab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amy.daily5vocab.ui.auth.LoginScreen
import com.amy.daily5vocab.ui.auth.RegisterScreen
import com.amy.daily5vocab.ui.theme.Daily5VocabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Daily5VocabTheme {
                val navController = rememberNavController()
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login") {
                            LoginScreen(
                                onNavigateToRegister = { navController.navigate("register") },
                                onLoginSuccess = { /* TODO: Navigate to Home */ }
                            )
                        }
                        composable("register") {
                            RegisterScreen(
                                onNavigateToLogin = { navController.navigate("login") },
                                onRegisterSuccess = { /* TODO: Navigate to Home */ }
                            )
                        }
                    }
                }
            }
        }
    }
}