package com.amy.daily5vocab.reminder

import android.content.Context

/**
 * Lightweight on-device store for the daily-reminder schedule. Kept separate from the
 * Firebase copy so background components (the boot receiver, the app launch reschedule)
 * can read the time synchronously without needing network or an authenticated session.
 */
object ReminderPreferences {
    private const val PREFS = "reminder_prefs"
    private const val KEY_ENABLED = "enabled"
    private const val KEY_HOUR = "hour"
    private const val KEY_MINUTE = "minute"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun save(context: Context, hour: Int, minute: Int) {
        prefs(context).edit()
            .putBoolean(KEY_ENABLED, true)
            .putInt(KEY_HOUR, hour)
            .putInt(KEY_MINUTE, minute)
            .apply()
    }

    fun isEnabled(context: Context): Boolean = prefs(context).getBoolean(KEY_ENABLED, false)

    fun hour(context: Context): Int = prefs(context).getInt(KEY_HOUR, 9)

    fun minute(context: Context): Int = prefs(context).getInt(KEY_MINUTE, 0)
}
