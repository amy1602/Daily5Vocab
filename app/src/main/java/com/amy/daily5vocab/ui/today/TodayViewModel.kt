package com.amy.daily5vocab.ui.today

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.amy.daily5vocab.data.history.HistoryRepository
import com.amy.daily5vocab.data.user.UserRepository
import com.amy.daily5vocab.data.words.WordBank

/** A word on the Today checklist plus whether the user has marked it learned. */
data class TodayWord(val id: String, val word: String, val learned: Boolean)

data class TodayUiState(
    val isLoading: Boolean = true,
    val topic: String = "",
    val words: List<TodayWord> = emptyList(),
    val errorMessage: String? = null,
) {
    val completed: Int get() = words.count { it.learned }
}

class TodayViewModel(
    private val repository: UserRepository = UserRepository(),
) : ViewModel() {

    var uiState by mutableStateOf(TodayUiState())
        private set

    init {
        load()
    }

    /**
     * Resolves the user's default topic, then loads today's words through
     * [HistoryRepository] (memory/DB first, refreshed from Firebase).
     */
    fun load() {
        repository.getTopics { result ->
            result
                .onSuccess { topics ->
                    val topic = topics.firstOrNull() ?: WordBank.DEFAULT_TOPIC
                    HistoryRepository.loadToday(topic) { words ->
                        uiState = TodayUiState(
                            isLoading = false,
                            topic = topic,
                            words = words.map { TodayWord(it.id, it.word, it.learned) },
                        )
                    }
                }
                .onFailure { error ->
                    uiState = TodayUiState(
                        isLoading = false,
                        errorMessage = error.message ?: "Couldn't load today's words.",
                    )
                }
        }
    }

    /** Toggles a word's learned state across memory, the local DB, and Firebase. */
    fun toggle(word: TodayWord) {
        HistoryRepository.toggleLearned(word.id, !word.learned)
        uiState = uiState.copy(
            words = HistoryRepository.todayWords().map { TodayWord(it.id, it.word, it.learned) },
        )
    }
}
