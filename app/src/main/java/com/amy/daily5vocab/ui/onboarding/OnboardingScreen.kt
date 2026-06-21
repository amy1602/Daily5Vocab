package com.amy.daily5vocab.ui.onboarding

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amy.daily5vocab.ui.auth.AuthBackground
import com.amy.daily5vocab.ui.auth.PrimaryButton
import com.amy.daily5vocab.ui.auth.clickableText
import com.amy.daily5vocab.ui.theme.BrandGreen
import com.amy.daily5vocab.ui.theme.BrandGreenDark
import com.amy.daily5vocab.ui.theme.Daily5VocabTheme
import com.amy.daily5vocab.ui.theme.FieldBackground
import com.amy.daily5vocab.ui.theme.NavyTitle
import com.amy.daily5vocab.ui.theme.SubtitleGray

/** A learning topic the user can pick during onboarding. */
data class Topic(val name: String, val icon: ImageVector)

private val Topics = listOf(
    Topic("Technology", Icons.Filled.Computer),
    Topic("Business", Icons.Filled.BusinessCenter),
    Topic("Travel", Icons.Filled.Flight),
    Topic("Cooking", Icons.Filled.Restaurant),
    Topic("Art", Icons.Filled.Palette),
    Topic("Science", Icons.Filled.Science),
)

@Composable
fun OnboardingScreen(
    onStartLearning: (selected: Set<String>) -> Unit = {},
) {
    var selected by remember { mutableStateOf(emptySet<String>()) }

    AuthBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "What do you want to learn?",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                color = BrandGreenDark,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Select your interests to get personalized daily words.",
                fontSize = 16.sp,
                color = SubtitleGray,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
            )

            Spacer(Modifier.height(28.dp))

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
                            onClick = {
                                selected = if (topic.name in selected) {
                                    selected - topic.name
                                } else {
                                    selected + topic.name
                                }
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            Spacer(Modifier.height(12.dp))

            PrimaryButton(
                text = "Start Learning",
                onClick = { onStartLearning(selected) },
            )
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
            .height(140.dp)
            .clickableText(onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = if (isSelected) BorderStroke(2.dp, BrandGreen) else null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Surface(
                shape = CircleShape,
                color = if (isSelected) BrandGreen.copy(alpha = 0.18f) else FieldBackground,
            ) {
                Box(
                    modifier = Modifier.size(52.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = topic.icon,
                        contentDescription = null,
                        tint = if (isSelected) BrandGreenDark else NavyTitle,
                        modifier = Modifier.size(26.dp),
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = topic.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = NavyTitle,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OnboardingScreenPreview() {
    Daily5VocabTheme {
        OnboardingScreen()
    }
}
