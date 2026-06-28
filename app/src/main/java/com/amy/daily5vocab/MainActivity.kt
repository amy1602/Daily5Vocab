package com.amy.daily5vocab

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.amy.daily5vocab.data.history.HistoryRepository
import com.amy.daily5vocab.data.user.UserCache
import com.amy.daily5vocab.reminder.ReminderNotifier
import com.amy.daily5vocab.reminder.ReminderScheduler
import com.amy.daily5vocab.ui.navigation.AppNavigation
import com.amy.daily5vocab.ui.theme.Daily5VocabTheme

class MainActivity : ComponentActivity() {

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* best effort */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        UserCache.init(this)
        HistoryRepository.init(this)
        ReminderNotifier.ensureChannel(this)
        // Re-apply any saved reminder schedule (alarms are cleared on reboot/reinstall).
        ReminderScheduler.rescheduleIfEnabled(this)
        requestNotificationPermissionIfNeeded()

        enableEdgeToEdge()
        setContent {
            Daily5VocabTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AppNavigation()
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
