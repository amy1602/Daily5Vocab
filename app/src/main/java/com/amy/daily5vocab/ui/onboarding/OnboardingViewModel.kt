package com.amy.daily5vocab.ui.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.amy.daily5vocab.data.user.UserRepository

data class OnboardingUiState(
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

class OnboardingViewModel(
    private val repository: UserRepository = UserRepository(),
) : ViewModel() {

    var uiState by mutableStateOf(OnboardingUiState())
        private set

    /**
     * Requires at least one topic, then persists the selection to Firebase.
     * The first selected topic becomes the user's default topic.
     *
     * Firestore's write completes locally right away and syncs to the server in the
     * background, so we proceed immediately rather than blocking on the server ack
     * (which never returns when the backend is offline/unreachable).
     */
    fun saveTopics(selected: Set<String>, onSaved: () -> Unit) {
        if (selected.isEmpty()) {
            uiState = OnboardingUiState(errorMessage = "Please select at least one topic.")
            return
        }
        repository.saveTopics(selected.toList()) { /* synced in the background */ }
        uiState = OnboardingUiState()
        onSaved()
    }

    fun clearError() {
        if (uiState.errorMessage != null) {
            uiState = uiState.copy(errorMessage = null)
        }
    }
}
