package com.amy.daily5vocab.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amy.daily5vocab.ui.theme.BrandGreenDark
import com.amy.daily5vocab.ui.theme.Daily5VocabTheme

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLogin: (email: String, password: String) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onForgotPassword: () -> Unit = {},
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AuthBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AuthHeader(
                title = "Welcome Back",
                subtitle = "Continue your learning journey today.",
            )

            Spacer(Modifier.height(32.dp))

            AuthCard {
                AuthTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    placeholder = "you@example.com",
                    leadingIcon = Icons.Filled.Email,
                    uppercaseLabel = true,
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
                )

                Spacer(Modifier.height(20.dp))

                AuthTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "••••••••",
                    leadingIcon = Icons.Filled.Lock,
                    uppercaseLabel = true,
                    isPassword = true,
                    trailingLabelContent = {
                        Text(
                            text = "Forgot password?",
                            color = BrandGreenDark,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            modifier = Modifier.clickableText(onForgotPassword),
                        )
                    },
                )

                if (errorMessage != null) {
                    Spacer(Modifier.height(16.dp))
                    AuthErrorText(errorMessage)
                }

                Spacer(Modifier.height(28.dp))

                PrimaryButton(
                    text = "Login",
                    onClick = { onLogin(email, password) },
                    isLoading = isLoading,
                )
            }

            Spacer(Modifier.height(24.dp))

            AuthFooter(
                prefix = "Don't have an account?",
                action = "Sign up",
                onActionClick = onNavigateToRegister,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    Daily5VocabTheme {
        LoginScreen(onNavigateToRegister = {}, onLogin = { _, _ -> })
    }
}
