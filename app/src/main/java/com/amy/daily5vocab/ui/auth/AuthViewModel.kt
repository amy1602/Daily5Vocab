package com.amy.daily5vocab.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.amy.daily5vocab.data.auth.AuthRepository

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository(),
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        val validation = validate(email = email, password = password)
        if (validation != null) {
            uiState = AuthUiState(errorMessage = validation)
            return
        }
        uiState = AuthUiState(isLoading = true)
        repository.login(email, password) { result ->
            handleResult(result, onSuccess)
        }
    }

    fun register(name: String, email: String, password: String, onSuccess: () -> Unit) {
        val validation = validate(name = name, email = email, password = password)
        if (validation != null) {
            uiState = AuthUiState(errorMessage = validation)
            return
        }
        uiState = AuthUiState(isLoading = true)
        repository.register(name, email, password) { result ->
            handleResult(result, onSuccess)
        }
    }

    fun clearError() {
        if (uiState.errorMessage != null) {
            uiState = uiState.copy(errorMessage = null)
        }
    }

    private fun handleResult(result: Result<Unit>, onSuccess: () -> Unit) {
        result
            .onSuccess {
                uiState = AuthUiState()
                onSuccess()
            }
            .onFailure { error ->
                uiState = AuthUiState(
                    errorMessage = error.message ?: "Something went wrong. Please try again.",
                )
            }
    }

    private fun validate(
        name: String? = null,
        email: String,
        password: String,
    ): String? {
        // Register-only constraints. `name != null` means this is a sign-up flow.
        val isRegister = name != null
        if (isRegister) {
            if (name!!.isBlank()) return "Please enter your name."
            if (name.trim().length < MIN_NAME_LENGTH) {
                return "Name must be at least $MIN_NAME_LENGTH characters."
            }
        }
        if (email.isBlank()) return "Please enter your email."
        if (!EMAIL_REGEX.matches(email.trim())) return "Please enter a valid email address."
        if (password.isBlank()) return "Please enter your password."
        if (password.length < MIN_PASSWORD_LENGTH) {
            return "Password must be at least $MIN_PASSWORD_LENGTH characters."
        }
        // Enforce password strength on sign-up only; login just has to match what exists.
        if (isRegister) {
            if (!password.any { it.isLetter() }) {
                return "Password must contain at least one letter."
            }
            if (!password.any { it.isDigit() }) {
                return "Password must contain at least one number."
            }
        }
        return null
    }

    private companion object {
        const val MIN_PASSWORD_LENGTH = 8
        const val MIN_NAME_LENGTH = 2
        val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
