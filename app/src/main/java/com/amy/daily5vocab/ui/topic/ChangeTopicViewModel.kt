package com.amy.daily5vocab.ui.topic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.amy.daily5vocab.data.user.UserRepository

data class ChangeTopicUiState(
    val selected: Set<String> = emptySet(),
    val original: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
) {
    /** True once the selection differs from what was loaded — gates the Save button. */
    val hasChanges: Boolean get() = selected != original
}

/**
 * Backs the "Change Topic" screen: loads the user's current topics so they start
 * pre-selected, tracks edits, and persists the new selection.
 */
class ChangeTopicViewModel(
    private val repository: UserRepository = UserRepository(),
) : ViewModel() {

    var uiState by mutableStateOf(ChangeTopicUiState())
        private set

    init {
        repository.getTopics { result ->
            val topics = result.getOrNull().orEmpty().toSet()
            uiState = uiState.copy(selected = topics, original = topics, isLoading = false)
        }
    }

    fun toggle(topic: String) {
        val current = uiState.selected
        uiState = uiState.copy(
            selected = if (topic in current) current - topic else current + topic,
            errorMessage = null,
        )
    }

    /**
     * Requires at least one topic, then persists the selection to Firebase.
     * Firestore applies the write locally right away and syncs in the background,
     * so we proceed immediately rather than blocking on the server ack.
     */
    fun save(onSaved: () -> Unit) {
        val selected = uiState.selected
        if (selected.isEmpty()) {
            uiState = uiState.copy(errorMessage = "Please select at least one topic.")
            return
        }
        repository.saveTopics(selected.toList()) { /* synced in the background */ }
        onSaved()
    }
}
