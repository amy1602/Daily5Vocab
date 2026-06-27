package com.amy.daily5vocab.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amy.daily5vocab.ui.auth.clickableText
import com.amy.daily5vocab.ui.theme.GrowthCard
import com.amy.daily5vocab.ui.theme.GrowthGreen
import com.amy.daily5vocab.ui.theme.GrowthGreenDeep
import com.amy.daily5vocab.ui.theme.GrowthSurface
import com.amy.daily5vocab.ui.theme.OxfordBlue
import com.amy.daily5vocab.ui.theme.OxfordSecondary
import com.amy.daily5vocab.ui.theme.SkyContainer
import com.amy.daily5vocab.ui.theme.SlateGray
import com.amy.daily5vocab.ui.theme.StreakFlame

/** Bottom navigation destinations shown on the main app shell. */
enum class AppTab(val label: String, val icon: ImageVector) {
    Today("Today", Icons.Filled.GridView),
    History("History", Icons.Filled.History),
    Settings("Settings", Icons.Filled.Settings),
}

/**
 * Shared app shell for the main tabbed screens: the "Daily 5 Vocab" header with the
 * streak badge, plus the bottom navigation bar. [content] receives the scaffold padding.
 */
@Composable
fun AppScaffold(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    streak: Int = 0,
    topBar: @Composable () -> Unit = { AppTopBar(streak = streak) },
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        containerColor = GrowthSurface,
        topBar = topBar,
        bottomBar = { AppBottomNav(selected = selectedTab, onTabSelected = onTabSelected) },
        content = content,
    )
}

/**
 * Top bar for secondary screens reached from a tab: a back arrow followed by the
 * screen [title]. Pair with [AppScaffold]'s `topBar` slot to keep the bottom nav.
 */
@Composable
fun AppBackTopBar(title: String, onBack: () -> Unit) {
    Surface(color = GrowthSurface) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickableText(onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = OxfordBlue,
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(Modifier.size(8.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = OxfordBlue,
            )
        }
    }
}

@Composable
private fun AppTopBar(streak: Int) {
    Surface(color = GrowthSurface) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = null,
                tint = GrowthGreenDeep,
                modifier = Modifier.size(24.dp),
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = "Daily 5 Vocab",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = GrowthGreenDeep,
            )
            Spacer(Modifier.weight(1f))
            StreakBadge(streak = streak)
        }
    }
}

@Composable
private fun StreakBadge(streak: Int) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(SkyContainer)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.LocalFireDepartment,
            contentDescription = "Current streak",
            tint = StreakFlame,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.size(4.dp))
        Text(
            text = "$streak",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = OxfordSecondary,
        )
    }
}

@Composable
private fun AppBottomNav(selected: AppTab, onTabSelected: (AppTab) -> Unit) {
    Surface(color = GrowthCard, shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppTab.entries.forEach { tab ->
                AppBottomNavItem(
                    tab = tab,
                    isSelected = tab == selected,
                    onClick = { onTabSelected(tab) },
                )
            }
        }
    }
}

@Composable
private fun AppBottomNavItem(tab: AppTab, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickableText(onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (isSelected) GrowthGreen else Color.Transparent)
                .padding(horizontal = 18.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = tab.icon,
                contentDescription = tab.label,
                tint = if (isSelected) Color.White else SlateGray,
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = tab.label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) GrowthGreenDeep else SlateGray,
        )
    }
}
