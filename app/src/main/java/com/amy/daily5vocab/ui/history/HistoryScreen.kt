package com.amy.daily5vocab.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.amy.daily5vocab.ui.theme.OxfordSecondary
import com.amy.daily5vocab.ui.theme.SkyContainer
import com.amy.daily5vocab.ui.theme.SkyTrack
import com.amy.daily5vocab.ui.theme.SlateGray
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HistoryScreen(
    groups: List<HistoryGroup>,
    selectedRange: HistoryRange,
    onRangeSelected: (HistoryRange) -> Unit,
    isLoading: Boolean = false,
    canLoadMore: Boolean = false,
    onLoadMore: () -> Unit = {},
    streak: Int = 5,
    selectedTab: AppTab = AppTab.History,
    onTabSelected: (AppTab) -> Unit = {},
) {
    val listState = rememberLazyListState()

    // Trigger a load when the last item scrolls into view.
    val reachedEnd by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            lastVisible >= listState.layoutInfo.totalItemsCount - 1
        }
    }
    LaunchedEffect(reachedEnd, canLoadMore, isLoading) {
        if (reachedEnd && canLoadMore && !isLoading) onLoadMore()
    }

    AppScaffold(
        selectedTab = selectedTab,
        onTabSelected = onTabSelected,
        streak = streak,
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                RangeChips(selected = selectedRange, onSelected = onRangeSelected)
            }

            val rangeIsRecent =
                selectedRange == HistoryRange.Today || selectedRange == HistoryRange.ThisWeek

            if (groups.isEmpty()) {
                item(key = "empty-range") {
                    Spacer(Modifier.height(20.dp))
                    if (rangeIsRecent) {
                        DateHeader(headerLabel(0))
                        Spacer(Modifier.height(12.dp))
                        EmptyCard(emptyMessage(HistoryRange.Today))
                    } else {
                        EmptyCard(emptyMessage(selectedRange))
                    }
                }
            } else {
                // For recent ranges, keep the design's "Today" empty state at the top.
                if (rangeIsRecent && groups.none { it.daysAgo == 0 }) {
                    item(key = "empty-today") {
                        Spacer(Modifier.height(20.dp))
                        DateHeader(headerLabel(0))
                        Spacer(Modifier.height(12.dp))
                        EmptyCard(emptyMessage(HistoryRange.Today))
                    }
                }
                groups.forEach { group ->
                    item(key = "header-${group.daysAgo}") {
                        Spacer(Modifier.height(20.dp))
                        DateHeader(headerLabel(group.daysAgo))
                        Spacer(Modifier.height(12.dp))
                    }
                    items(
                        count = group.entries.size,
                        key = { i -> "${group.daysAgo}-${group.entries[i].word}" },
                    ) { i ->
                        HistoryWordCard(group.entries[i])
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }

            item {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = GrowthGreenDeep, strokeWidth = 2.dp)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun RangeChips(selected: HistoryRange, onSelected: (HistoryRange) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        HistoryRange.entries.forEach { range ->
            RangeChip(
                label = range.label,
                isSelected = range == selected,
                onClick = { onSelected(range) },
            )
        }
    }
}

@Composable
private fun RangeChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (isSelected) GrowthGreen else GrowthCard)
            .then(if (isSelected) Modifier else Modifier.border(1.dp, SkyTrack, CircleShape))
            .clickableText(onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else SlateGray,
        )
    }
}

@Composable
private fun HistoryWordCard(entry: HistoryEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GrowthCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Matches the Today tab's word size.
                Text(
                    text = entry.word,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = OxfordBlue,
                )
                Spacer(Modifier.height(8.dp))
                TopicChip(entry.topic)
            }
            LearnedCheck()
        }
    }
}

@Composable
private fun TopicChip(topic: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SkyContainer)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = topic,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = OxfordSecondary,
        )
    }
}

@Composable
private fun LearnedCheck() {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(GrowthGreen.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = "Learned",
            tint = GrowthGreenDeep,
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
private fun DateHeader(label: String) {
    Text(
        text = label,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        color = SlateGray,
    )
}

/** Empty-state message worded for the selected filter range. */
private fun emptyMessage(range: HistoryRange): String = when (range) {
    HistoryRange.Today -> "You haven't learned any words yet today!"
    HistoryRange.ThisWeek -> "You haven't learned any words yet this week!"
    HistoryRange.ThisMonth -> "You haven't learned any words yet for this month!"
    HistoryRange.ThisYear -> "You haven't learned any words yet for this year!"
}

@Composable
private fun EmptyCard(message: String) {
    val borderColor = SkyTrack
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SkyContainer.copy(alpha = 0.35f))
            .drawBehind {
                drawRoundRect(
                    color = borderColor,
                    cornerRadius = CornerRadius(16.dp.toPx()),
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f),
                    ),
                )
            }
            .padding(horizontal = 24.dp, vertical = 28.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.StarBorder,
                contentDescription = null,
                tint = SlateGray,
                modifier = Modifier.size(32.dp),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = SlateGray,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** Builds a section header like "Today", "Yesterday, Oct 24", or "Monday, Oct 23". */
private fun headerLabel(daysAgo: Int): String {
    val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -daysAgo) }
    val date = SimpleDateFormat("MMM d", Locale.getDefault()).format(calendar.time)
    return when (daysAgo) {
        0 -> "Today"
        1 -> "Yesterday, $date"
        else -> {
            val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
            "$dayOfWeek, $date"
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HistoryScreenPreview() {
    Daily5VocabTheme {
        HistoryScreen(
            groups = listOf(
                HistoryGroup(0, emptyList()),
                HistoryGroup(
                    1,
                    listOf(
                        HistoryEntry("Synergy", "Business"),
                        HistoryEntry("Ubiquitous", "General"),
                    ),
                ),
            ),
            selectedRange = HistoryRange.ThisWeek,
            onRangeSelected = {},
        )
    }
}
