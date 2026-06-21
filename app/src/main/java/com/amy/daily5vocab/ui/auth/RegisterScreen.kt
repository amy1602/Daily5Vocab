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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.amy.daily5vocab.ui.theme.Daily5VocabTheme

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegister: (name: String, email: String, password: String) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
) {
    var name by remember { mutableStateOf("") }
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
                title = "Start Your Journey",
                subtitle = "Commit to mastering 5 new words every day. " +
                    "Simple, effective, and habit-forming.",
            )

            Spacer(Modifier.height(32.dp))

            AuthCard {
                AuthTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Full Name",
                    placeholder = "Alex Reader",
                    leadingIcon = Icons.Filled.Person,
                )

                Spacer(Modifier.height(20.dp))

                AuthTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address",
                    placeholder = "alex@example.com",
                    leadingIcon = Icons.Filled.Email,
                    keyboardType = KeyboardType.Email,
                )

                Spacer(Modifier.height(20.dp))

                AuthTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "••••••••",
                    leadingIcon = Icons.Filled.Lock,
                    isPassword = true,
                )

                if (errorMessage != null) {
                    Spacer(Modifier.height(16.dp))
                    AuthErrorText(errorMessage)
                }

                Spacer(Modifier.height(28.dp))

                PrimaryButton(
                    text = "Create Account",
                    onClick = { onRegister(name, email, password) },
                    trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                    isLoading = isLoading,
                )
            }

            Spacer(Modifier.height(24.dp))

            AuthFooter(
                prefix = "Already have an account?",
                action = "Log in",
                onActionClick = onNavigateToLogin,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RegisterScreenPreview() {
    Daily5VocabTheme {
        RegisterScreen(onNavigateToLogin = {}, onRegister = { _, _, _ -> })
    }
}
