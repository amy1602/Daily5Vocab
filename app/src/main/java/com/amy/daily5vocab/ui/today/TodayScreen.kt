package com.amy.daily5vocab.ui.today

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.amy.daily5vocab.ui.theme.SkyTrack
import com.amy.daily5vocab.ui.theme.SlateGray

/**
 * The daily "checklist" home screen: streak header, 5-a-day progress ring,
 * today's topic, and the list of words to learn.
 */
@Composable
fun TodayScreen(
    topic: String = "Business",
    words: List<TodayWord> = emptyList(),
    streak: Int = 5,
    isLoading: Boolean = false,
    selectedTab: AppTab = AppTab.Today,
    onToggleWord: (TodayWord) -> Unit = {},
    onOpenWord: (TodayWord) -> Unit = {},
    onTabSelected: (AppTab) -> Unit = {},
) {
    val completed = words.count { it.learned }
    AppScaffold(
        selectedTab = selectedTab,
        onTabSelected = onTabSelected,
        streak = streak,
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = GrowthGreenDeep)
            }
            return@AppScaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(16.dp))

            ProgressRing(completed = completed, total = words.size)

            Spacer(Modifier.height(18.dp))

            Text(
                text = "Topic: $topic",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = OxfordBlue,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Take a few minutes to learn today's curated vocabulary. " +
                    "Consistent small steps lead to mastery.",
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = SlateGray,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(20.dp))

            words.forEach { word ->
                WordCard(
                    word = word,
                    onToggle = { onToggleWord(word) },
                    onOpen = { onOpenWord(word) },
                )
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ProgressRing(completed: Int, total: Int) {
    val fraction = if (total == 0) 0f else completed.toFloat() / total
    Box(
        modifier = Modifier.size(140.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 12.dp.toPx()
            val inset = stroke / 2
            val arcSize = Size(size.width - stroke, size.height - stroke)
            // Full ring acts as the track (Soft Sky tint kept green-forward per design).
            drawArc(
                color = GrowthGreen,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            // Completed progress drawn in the deeper Learning Green.
            if (fraction > 0f) {
                drawArc(
                    color = GrowthGreenDeep,
                    startAngle = -90f,
                    sweepAngle = 360f * fraction,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$completed/$total",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = GrowthGreenDeep,
            )
            Text(
                text = "COMPLETED",
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                color = SlateGray,
            )
        }
    }
}

@Composable
private fun WordCard(word: TodayWord, onToggle: () -> Unit, onOpen: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickableText(onOpen),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GrowthCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (word.learned) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(GrowthGreen)
                        .clickableText(onToggle),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Learned",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp),
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .border(2.dp, SkyTrack, CircleShape)
                        .clickableText(onToggle),
                )
            }
            Spacer(Modifier.size(14.dp))
            Text(
                text = word.word,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = OxfordBlue,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TodayScreenPreview() {
    Daily5VocabTheme {
        TodayScreen()
    }
}
