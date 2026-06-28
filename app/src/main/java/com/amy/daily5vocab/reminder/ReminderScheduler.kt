package com.amy.daily5vocab.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

/**
 * Schedules the daily learning reminder via [AlarmManager]. Uses an exact, Doze-friendly
 * one-shot alarm that the [ReminderReceiver] re-arms for the next day after it fires, so
 * the notification lands at the chosen time rather than whenever the system next batches.
 */
object ReminderScheduler {
    private const val REQUEST_CODE = 5001

    /** (Re)schedules the daily alarm at [hour]:[minute] and persists it for reboot recovery. */
    fun schedule(context: Context, hour: Int, minute: Int) {
        ReminderPreferences.save(context, hour, minute)
        arm(context, nextTriggerAt(hour, minute))
    }

    /** Re-applies the saved schedule if a reminder is enabled (app launch / boot / after firing). */
    fun rescheduleIfEnabled(context: Context) {
        if (ReminderPreferences.isEnabled(context)) {
            arm(context, nextTriggerAt(ReminderPreferences.hour(context), ReminderPreferences.minute(context)))
        }
    }

    private fun arm(context: Context, triggerAtMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = pendingIntent(context)
        if (canScheduleExact(alarmManager)) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent,
            )
        } else {
            // Exact alarms not permitted by the user: still fire (in Doze), just less precisely.
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent,
            )
        }
    }

    /** Whether the app may schedule exact alarms (always true below Android 12). */
    fun canScheduleExact(alarmManager: AlarmManager): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }

    private fun pendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    /** Next occurrence of [hour]:[minute]; rolls to tomorrow if today's time already passed. */
    private fun nextTriggerAt(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= now.timeInMillis) add(Calendar.DAY_OF_MONTH, 1)
        }.timeInMillis
    }
}
