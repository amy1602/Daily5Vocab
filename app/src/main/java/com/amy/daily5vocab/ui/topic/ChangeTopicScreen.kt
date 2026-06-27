package com.amy.daily5vocab.ui.topic

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amy.daily5vocab.ui.auth.PrimaryButton
import com.amy.daily5vocab.ui.auth.clickableText
import com.amy.daily5vocab.ui.common.AppBackTopBar
import com.amy.daily5vocab.ui.common.AppScaffold
import com.amy.daily5vocab.ui.common.AppTab
import com.amy.daily5vocab.ui.onboarding.Topic
import com.amy.daily5vocab.ui.theme.Daily5VocabTheme
import com.amy.daily5vocab.ui.theme.GrowthCard
import com.amy.daily5vocab.ui.theme.GrowthGreen
import com.amy.daily5vocab.ui.theme.GrowthGreenDeep
import com.amy.daily5vocab.ui.theme.OxfordBlue
import com.amy.daily5vocab.ui.theme.OxfordSecondary
import com.amy.daily5vocab.ui.theme.SkyContainer
import com.amy.daily5vocab.ui.theme.SlateGray

/** Topics offered on the Change Topic screen, in the order shown in the design. */
private val Topics = listOf(
    Topic("Business", Icons.Filled.BusinessCenter),
    Topic("Technology", Icons.Filled.Computer),
    Topic("Travel", Icons.Filled.Flight),
    Topic("Cooking", Icons.Filled.Restaurant),
    Topic("Art", Icons.Filled.Palette),
    Topic("Science", Icons.Filled.Science),
    Topic("Literature", Icons.AutoMirrored.Filled.MenuBook),
    Topic("Psychology", Icons.Filled.Psychology),
)

/**
 * Lets the user re-pick the topics that shape their daily 5 words. Reached from the
 * Settings "Change Topics" row; keeps the bottom nav with Settings selected.
 */
@Composable
fun ChangeTopicScreen(
    selected: Set<String>,
    onToggle: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    canSave: Boolean = true,
    isSaving: Boolean = false,
    errorMessage: String? = null,
    selectedTab: AppTab = AppTab.Settings,
    onTabSelected: (AppTab) -> Unit = {},
) {
    AppScaffold(
        selectedTab = selectedTab,
        onTabSelected = onTabSelected,
        topBar = { AppBackTopBar(title = "Change Topic", onBack = onBack) },
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
                text = "Choose your focus",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = OxfordBlue,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Tailor your daily 5 words to align with your personal or professional interests.",
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = SlateGray,
            )

            Spacer(Modifier.height(24.dp))

            // 2-column grid built from rows of two so the screen can scroll naturally.
            Topics.chunked(2).forEach { rowTopics ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    rowTopics.forEach { topic ->
                        TopicCard(
                            topic = topic,
                            isSelected = topic.name in selected,
                            onClick = { onToggle(topic.name) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            Spacer(Modifier.height(8.dp))

            FlexibilityCard()

            if (errorMessage != null) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    color = Color(0xFFBA1A1A),
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(24.dp))

            PrimaryButton(
                text = "Save Selection",
                onClick = onSave,
                trailingIcon = Icons.Filled.Check,
                isLoading = isSaving,
                enabled = canSave && selected.isNotEmpty(),
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TopicCard(
    topic: Topic,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .height(118.dp)
            .clickableText(onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GrowthCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (isSelected) BorderStroke(2.dp, GrowthGreen) else null,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) GrowthGreen.copy(alpha = 0.15f) else SkyContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = topic.icon,
                        contentDescription = null,
                        tint = if (isSelected) GrowthGreenDeep else OxfordSecondary,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = topic.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OxfordBlue,
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(GrowthGreenDeep),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun FlexibilityCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GrowthCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SkyContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = null,
                    tint = OxfordSecondary,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Flexibility",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = OxfordBlue,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Switch topics at any time to broaden your vocabulary range.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = SlateGray,
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ChangeTopicScreenPreview() {
    Daily5VocabTheme {
        ChangeTopicScreen(
            selected = setOf("Business"),
            onToggle = {},
            onSave = {},
            onBack = {},
        )
    }
}
