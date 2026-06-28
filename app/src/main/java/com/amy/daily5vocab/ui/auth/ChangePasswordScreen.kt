package com.amy.daily5vocab.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amy.daily5vocab.ui.theme.BrandGreen
import com.amy.daily5vocab.ui.theme.BrandGreenDark
import com.amy.daily5vocab.ui.theme.Daily5VocabTheme
import com.amy.daily5vocab.ui.theme.FieldBackground

@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit,
    onSave: (current: String, new: String, confirm: String) -> Unit,
    state: ChangePasswordUiState = ChangePasswordUiState(),
    onCurrentBlur: (current: String) -> Unit = {},
    onNewBlur: (new: String, confirm: String) -> Unit = { _, _ -> },
    onConfirmBlur: (new: String, confirm: String) -> Unit = { _, _ -> },
) {
    var current by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    AuthBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            ChangePasswordTopBar(onBack = onBack)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(BrandGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.LockReset,
                        contentDescription = null,
                        tint = BrandGreenDark,
                        modifier = Modifier.size(44.dp),
                    )
                }

                Spacer(Modifier.height(20.dp))

                AuthHeader(
                    subtitle = "Keep your progress safe by choosing a strong, " +
                        "unique password.",
                )

                Spacer(Modifier.height(28.dp))

                AuthCard {
                    AuthTextField(
                        value = current,
                        onValueChange = { current = it },
                        label = "Current Password",
                        placeholder = "Enter your current password",
                        leadingIcon = Icons.Filled.Key,
                        uppercaseLabel = true,
                        isPassword = true,
                        errorText = state.currentError,
                        onFocusLost = { onCurrentBlur(current) },
                    )

                    Spacer(Modifier.height(20.dp))

                    AuthTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = "New Password",
                        placeholder = "Min. 8 characters",
                        leadingIcon = Icons.Filled.Lock,
                        uppercaseLabel = true,
                        isPassword = true,
                        errorText = state.newError,
                        onFocusLost = { onNewBlur(newPassword, confirm) },
                    )

                    Spacer(Modifier.height(12.dp))

                    PasswordStrengthBar(newPassword)

                    Spacer(Modifier.height(20.dp))

                    AuthTextField(
                        value = confirm,
                        onValueChange = { confirm = it },
                        label = "Confirm New Password",
                        placeholder = "Repeat new password",
                        leadingIcon = Icons.Filled.VerifiedUser,
                        uppercaseLabel = true,
                        isPassword = true,
                        errorText = state.confirmError,
                        onFocusLost = { onConfirmBlur(newPassword, confirm) },
                    )

                    if (state.generalError != null) {
                        Spacer(Modifier.height(16.dp))
                        AuthErrorText(state.generalError)
                    }

                    Spacer(Modifier.height(28.dp))

                    PrimaryButton(
                        text = "Save Password",
                        onClick = { onSave(current, newPassword, confirm) },
                        isLoading = state.isLoading,
                    )
                }
            }
        }
    }
}

@Composable
private fun ChangePasswordTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickableText(onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = BrandGreenDark,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(Modifier.size(8.dp))
        Text(
            text = "Change Password",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = BrandGreenDark,
        )
    }
}

/** Four-segment meter that fills and shifts colour as the new password gets stronger. */
@Composable
private fun PasswordStrengthBar(password: String) {
    val score = passwordStrength(password)
    val filledColor = when (score) {
        in 0..1 -> Color(0xFFE53935) // weak
        2 -> Color(0xFFF59E0B)       // fair
        3 -> Color(0xFF7CB342)       // good
        else -> BrandGreenDark       // strong
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        repeat(4) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (index < score) filledColor else FieldBackground),
            )
        }
    }
}

/** Rough 0–4 strength score: length, letters, digits, and symbols/case variety. */
private fun passwordStrength(password: String): Int {
    if (password.isEmpty()) return 0
    var score = 0
    if (password.length >= 8) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { it.isLetter() }) score++
    if (password.any { !it.isLetterOrDigit() } || password.any { it.isUpperCase() }) score++
    return score
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ChangePasswordScreenPreview() {
    Daily5VocabTheme {
        ChangePasswordScreen(onBack = {}, onSave = { _, _, _ -> })
    }
}
