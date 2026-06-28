package com.amy.daily5vocab.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amy.daily5vocab.ui.auth.clickableText
import com.amy.daily5vocab.ui.common.AppScaffold
import com.amy.daily5vocab.ui.common.AppTab
import com.amy.daily5vocab.ui.theme.Daily5VocabTheme
import com.amy.daily5vocab.ui.theme.GrowthCard
import com.amy.daily5vocab.ui.theme.GrowthGreen
import com.amy.daily5vocab.ui.theme.GrowthGreenDeep
import com.amy.daily5vocab.ui.theme.OxfordBlue
import com.amy.daily5vocab.ui.theme.OxfordSecondary
import com.amy.daily5vocab.ui.theme.SkyContainer
import com.amy.daily5vocab.ui.theme.SkyTrack
import com.amy.daily5vocab.ui.theme.SlateGray

private val ErrorRed = Color(0xFFBA1A1A)

/**
 * Settings screen: learning preferences, notification options, and account actions,
 * grouped into tonal cards per the "Growth & Clarity" design system.
 */
@Composable
fun SettingsScreen(
    difficulty: String = "Intermediate",
    reminderTime: String = "09:00 AM",
    streak: Int = 5,
    selectedTab: AppTab = AppTab.Settings,
    onTabSelected: (AppTab) -> Unit = {},
    onChangeTopics: () -> Unit = {},
    onChangeDifficulty: () -> Unit = {},
    onReminderTimeSelected: (String) -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onSignOut: () -> Unit = {},
) {
    var showTimePicker by remember { mutableStateOf(false) }

    AppScaffold(
        selectedTab = selectedTab,
        onTabSelected = onTabSelected,
        streak = streak,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            Text(
                text = "Settings",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = OxfordBlue,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Manage your learning preferences and account details.",
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = SlateGray,
            )

            SectionHeader("Learning Preferences")
            SettingsCard {
                SettingRow(
                    icon = Icons.Filled.Category,
                    iconBackground = GrowthGreen.copy(alpha = 0.15f),
                    iconTint = GrowthGreenDeep,
                    title = "Change Topics",
                    subtitle = "Select categories like Science, Business, or General",
                    onClick = onChangeTopics,
                )
                CardDivider()
                SettingRow(
                    icon = Icons.Filled.Tune,
                    iconBackground = SkyContainer,
                    iconTint = OxfordSecondary,
                    title = "Difficulty Level",
                    subtitle = "Currently set to $difficulty",
                    onClick = onChangeDifficulty,
                )
            }

            SectionHeader("Notifications")
            SettingsCard {
                SettingRow(
                    icon = Icons.Filled.Notifications,
                    iconBackground = GrowthGreen.copy(alpha = 0.15f),
                    iconTint = GrowthGreenDeep,
                    title = "Daily Reminders",
                    subtitle = "Don't miss your 5 words a day",
                    showChevron = false,
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                ) {
                    Text(
                        text = "Reminder Time",
                        fontSize = 13.sp,
                        color = SlateGray,
                    )
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SkyContainer)
                            .clickableText { showTimePicker = true }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    ) {
                        Text(
                            text = reminderTime,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OxfordBlue,
                        )
                    }
                }
            }

            SectionHeader("Account")
            SettingsCard {
                SettingRow(
                    icon = Icons.Filled.Person,
                    title = "Profile",
                    subtitle = "Update password",
                    onClick = onOpenProfile,
                )
                CardDivider()
                SettingRow(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    iconTint = ErrorRed,
                    title = "Sign Out",
                    titleColor = ErrorRed,
                    showChevron = false,
                    onClick = onSignOut,
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    if (showTimePicker) {
        ReminderTimeDialog(
            initial = reminderTime,
            onConfirm = {
                showTimePicker = false
                onReminderTimeSelected(it)
            },
            onDismiss = { showTimePicker = false },
        )
    }
}

/** Clock dialog for picking the daily reminder time, seeded from the current [initial] value. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderTimeDialog(
    initial: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val (initialHour, initialMinute) = parseTime(initial)
    val state = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false,
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reminder Time", fontWeight = FontWeight.Bold, color = OxfordBlue) },
        text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TimePicker(state = state)
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(formatTime(state.hour, state.minute)) }) {
                Text("OK", color = GrowthGreenDeep, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SlateGray)
            }
        },
    )
}

@Composable
private fun SectionHeader(text: String) {
    Spacer(Modifier.height(24.dp))
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = OxfordBlue,
    )
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GrowthCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(content = { content() })
    }
}

@Composable
private fun CardDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 16.dp),
        color = SkyTrack,
        thickness = 1.dp,
    )
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit = {},
    iconBackground: Color? = null,
    iconTint: Color = SlateGray,
    titleColor: Color = OxfordBlue,
    showChevron: Boolean = true,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableText(onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (iconBackground != null) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBackground),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.size(14.dp))
        } else {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            Spacer(Modifier.size(16.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = titleColor,
            )
            if (subtitle != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = SlateGray,
                )
            }
        }

        if (showChevron) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = SlateGray,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SettingsScreenPreview() {
    Daily5VocabTheme {
        SettingsScreen()
    }
}
