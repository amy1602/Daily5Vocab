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

    /** Loads the user's default topic from Firebase and samples 5 random words from it. */
    fun load() {
        uiState = TodayUiState(isLoading = true)
        repository.getTopics { result ->
            result
                .onSuccess { topics ->
                    val topic = topics.firstOrNull() ?: WordBank.DEFAULT_TOPIC
                    uiState = TodayUiState(
                        isLoading = false,
                        topic = topic,
                        words = WordBank.pickWords(topic),
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
