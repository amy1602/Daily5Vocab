package com.amy.daily5vocab.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.amy.daily5vocab.data.auth.AuthRepository

data class ChangePasswordUiState(
    val isLoading: Boolean = false,
    val verifyingCurrent: Boolean = false,
    val currentVerified: Boolean = false,
    val currentError: String? = null,
    val newError: String? = null,
    val confirmError: String? = null,
    val generalError: String? = null,
)

class ChangePasswordViewModel(
    private val repository: AuthRepository = AuthRepository(),
) : ViewModel() {

    var uiState by mutableStateOf(ChangePasswordUiState())
        private set

    /** Verifies the current password against Firebase when its field loses focus. */
    fun verifyCurrentPassword(current: String) {
        if (current.isBlank()) {
            uiState = uiState.copy(currentVerified = false, currentError = null)
            return
        }
        uiState = uiState.copy(verifyingCurrent = true, currentError = null)
        repository.verifyPassword(current) { result ->
            uiState = result.fold(
                onSuccess = {
                    uiState.copy(verifyingCurrent = false, currentVerified = true, currentError = null)
                },
                onFailure = { error ->
                    uiState.copy(
                        verifyingCurrent = false,
                        currentVerified = false,
                        currentError = error.message ?: AuthRepository.INCORRECT_CURRENT_PASSWORD,
                    )
                },
            )
        }
    }

    /** Validates the new password against the same rules used at registration, on blur. */
    fun validateNewPassword(new: String, confirm: String) {
        uiState = uiState.copy(
            newError = newPasswordError(new),
            // Re-check the match once a confirm value already exists.
            confirmError = if (confirm.isEmpty()) uiState.confirmError else confirmError(new, confirm),
        )
    }

    fun validateConfirmPassword(new: String, confirm: String) {
        uiState = uiState.copy(confirmError = confirmError(new, confirm))
    }

    fun changePassword(current: String, new: String, confirm: String, onSuccess: () -> Unit) {
        val newErr = newPasswordError(new) ?: if (new.isBlank()) "Please enter a new password." else null
        val confirmErr = confirmError(new, confirm)
        val currentErr = if (current.isBlank()) "Please enter your current password." else null
        if (newErr != null || confirmErr != null || currentErr != null) {
            uiState = uiState.copy(
                currentError = currentErr ?: uiState.currentError,
                newError = newErr,
                confirmError = confirmErr,
                generalError = null,
            )
            return
        }
        uiState = uiState.copy(isLoading = true, generalError = null)
        repository.changePassword(current, new) { result ->
            result
                .onSuccess {
                    uiState = ChangePasswordUiState()
                    onSuccess()
                }
                .onFailure { error ->
                    val message = error.message ?: "Something went wrong. Please try again."
                    // Route an incorrect-current-password error to that field; others are general.
                    uiState = if (message == AuthRepository.INCORRECT_CURRENT_PASSWORD) {
                        uiState.copy(isLoading = false, currentVerified = false, currentError = message)
                    } else {
                        uiState.copy(isLoading = false, generalError = message)
                    }
                }
        }
    }

    /** Null when valid; otherwise the register-equivalent rule that failed. */
    private fun newPasswordError(new: String): String? {
        if (new.isEmpty()) return null // don't nag while the field is still empty
        if (new.length < MIN_PASSWORD_LENGTH) {
            return "Password must be at least $MIN_PASSWORD_LENGTH characters."
        }
        if (!new.any { it.isLetter() }) return "Password must contain at least one letter."
        if (!new.any { it.isDigit() }) return "Password must contain at least one number."
        return null
    }

    private fun confirmError(new: String, confirm: String): String? =
        if (confirm.isNotEmpty() && new != confirm) "Passwords do not match." else null

    private companion object {
        const val MIN_PASSWORD_LENGTH = 8
    }
}
