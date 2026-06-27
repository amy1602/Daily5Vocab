package com.amy.daily5vocab.ui.today

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.amy.daily5vocab.data.user.UserRepository
import com.amy.daily5vocab.data.words.WordBank

data class TodayUiState(
    val isLoading: Boolean = true,
    val topic: String = "",
    val words: List<String> = emptyList(),
    val errorMessage: String? = null,
)

class TodayViewModel(
    private val repository: UserRepository = UserRepository(),
) : ViewModel() {

    var uiState by mutableStateOf(TodayUiState())
        private set

    init {
        load()
    }

    /**
     * Loads the user's default topic (the first saved one) from Firebase. Re-samples the
     * 5 words only when that topic changed, so returning to this screen — e.g. after editing
     * topics in Settings — reflects the new focus without reshuffling an unchanged day's set.
     */
    fun load() {
        repository.getTopics { result ->
            result
                .onSuccess { topics ->
                    val topic = topics.firstOrNull() ?: WordBank.DEFAULT_TOPIC
                    val keepWords = topic == uiState.topic && uiState.words.isNotEmpty()
                    uiState = TodayUiState(
                        isLoading = false,
                        topic = topic,
                        words = if (keepWords) uiState.words else WordBank.pickWords(topic),
                    )
                }
                .onFailure { error ->
                    uiState = TodayUiState(
                        isLoading = false,
                        errorMessage = error.message ?: "Couldn't load today's words.",
                    )
                }
        }
    }
}
