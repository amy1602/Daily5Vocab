package com.amy.daily5vocab.ui.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.amy.daily5vocab.data.history.HistoryRepository
import com.amy.daily5vocab.data.history.LearnedWord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** A learned word as shown in history. [id] opens its detail view ("{date}_{word}"). */
data class HistoryEntry(val id: String, val word: String, val topic: String)

/** Words learned on a given day, expressed as a [daysAgo] offset from today. */
data class HistoryGroup(val daysAgo: Int, val entries: List<HistoryEntry>)

/** Time windows offered by the filter chips, in display order. */
enum class HistoryRange(val label: String, val maxDaysAgo: Int) {
    ThisWeek("This Week", 7),
    Today("Today", 0),
    ThisMonth("This Month", 31),
    ThisYear("This Year", 365),
}

/**
 * Backs the History screen. Reads learned words from [HistoryRepository]: within a session
 * it reuses the in-memory cache (so toggles made on Today appear immediately); the first
 * time, it pages DB-first and refreshes from Firebase. Scrolling to the end loads more.
 */
class HistoryViewModel : ViewModel() {

    var selectedRange by mutableStateOf(HistoryRange.ThisWeek)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var canLoadMore by mutableStateOf(true)
        private set

    private var entries by mutableStateOf<List<LearnedWord>>(emptyList())

    init {
        if (HistoryRepository.isHistoryLoaded()) {
            entries = HistoryRepository.snapshot()
            canLoadMore = HistoryRepository.hasMore()
        } else {
            loadMore()
        }
    }

    fun loadMore() {
        if (isLoading || !canLoadMore) return
        isLoading = true
        HistoryRepository.loadHistoryPage { list, more ->
            entries = list
            canLoadMore = more
            isLoading = false
        }
    }

    fun selectRange(range: HistoryRange) {
        selectedRange = range
    }

    /** Non-empty learned-word groups within the selected range, most recent first. */
    val groups: List<HistoryGroup>
        get() = entries
            .filter { it.learned }
            .groupBy { it.date }
            .mapNotNull { (date, words) ->
                val days = daysAgo(date)
                if (days in 0..selectedRange.maxDaysAgo) {
                    HistoryGroup(days, words.map { HistoryEntry(it.id, it.word, it.topic) })
                } else {
                    null
                }
            }
            .sortedBy { it.daysAgo }

    private fun daysAgo(date: String): Int {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val then = format.parse(date)?.time ?: return Int.MAX_VALUE
        val now = format.parse(format.format(Date()))?.time ?: return Int.MAX_VALUE
        return ((now - then) / 86_400_000L).toInt()
    }
}
