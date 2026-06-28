package com.amy.daily5vocab.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Fired by the daily alarm; posts the reminder notification and re-arms for tomorrow. */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        ReminderNotifier.showDailyReminder(context)
        // Exact alarms are one-shot, so schedule the next day's reminder.
        ReminderScheduler.rescheduleIfEnabled(context)
    }
}
