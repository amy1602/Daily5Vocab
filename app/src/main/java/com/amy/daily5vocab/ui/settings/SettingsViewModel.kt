package com.amy.daily5vocab.ui.settings

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.amy.daily5vocab.data.user.UserRepository
import com.amy.daily5vocab.reminder.ReminderScheduler

data class SettingsUiState(
    val reminderTime: String = "09:00 AM",
)

/**
 * Backs the Settings screen. Owns the daily-reminder time: keeps it in sync with Firebase
 * and (re)schedules the local alarm that posts the reminder notification.
 */
class SettingsViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val repository = UserRepository()

    var uiState by mutableStateOf(SettingsUiState())
        private set

    init {
        repository.getReminderTime { result ->
            result.getOrNull()?.let { time ->
                uiState = uiState.copy(reminderTime = time)
                // Re-apply the schedule locally (e.g. after a reinstall that cleared prefs).
                scheduleReminder(time)
            }
        }
    }

    /** Updates the displayed time, persists it to Firebase, and schedules the daily alarm. */
    fun setReminderTime(time: String) {
        uiState = uiState.copy(reminderTime = time)
        repository.saveReminderTime(time) { /* synced in the background */ }
        scheduleReminder(time)
    }

    private fun scheduleReminder(time: String) {
        val (hour, minute) = parseTime(time)
        ReminderScheduler.schedule(getApplication(), hour, minute)
    }
}
