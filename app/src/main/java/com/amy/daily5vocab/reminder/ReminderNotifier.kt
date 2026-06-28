package com.amy.daily5vocab.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.amy.daily5vocab.MainActivity
import com.amy.daily5vocab.R
import com.amy.daily5vocab.data.words.WordBank

/**
 * Builds and posts the daily "learn your 5 words" notification. The expanded view lists
 * five term/meaning pairs (e.g. "Resilient: Kiên cường"), per the reminder design.
 */
object ReminderNotifier {
    const val CHANNEL_ID = "daily_reminder"
    private const val NOTIFICATION_ID = 5002

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Daily reminders",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Reminds you to learn your 5 words each day."
            }
            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    fun showDailyReminder(context: Context) {
        ensureChannel(context)
        if (!canPostNotifications(context)) return

        val words = WordBank.pickWords(WordBank.DEFAULT_TOPIC, count = 5)
        val body = words.joinToString("\n") { "${it.term}: ${it.meaning}" }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_monochrome)
            .setContentTitle("Daily 5 Vocab")
            .setContentText("Your 5 words for today are ready to learn.")
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(openAppIntent(context))
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    private fun openAppIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    /** Android 13+ requires the POST_NOTIFICATIONS runtime permission before posting. */
    private fun canPostNotifications(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }
}
