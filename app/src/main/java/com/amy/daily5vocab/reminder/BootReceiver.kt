package com.amy.daily5vocab.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Re-applies the daily reminder alarm after a reboot, since alarms don't survive it. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            ReminderScheduler.rescheduleIfEnabled(context)
        }
    }
}
