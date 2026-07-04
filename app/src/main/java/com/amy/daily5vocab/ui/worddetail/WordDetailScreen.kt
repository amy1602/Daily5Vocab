package com.amy.daily5vocab.ui.worddetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.speech.tts.TextToSpeech
import com.amy.daily5vocab.ui.auth.clickableText
import com.amy.daily5vocab.ui.theme.Daily5VocabTheme
import com.amy.daily5vocab.ui.theme.GrowthCard
import com.amy.daily5vocab.ui.theme.GrowthGreen
import com.amy.daily5vocab.ui.theme.GrowthGreenDeep
import com.amy.daily5vocab.ui.theme.GrowthSurface
import com.amy.daily5vocab.ui.theme.OxfordBlue
import com.amy.daily5vocab.ui.theme.OxfordSecondary
import com.amy.daily5vocab.ui.theme.SkyContainer
import com.amy.daily5vocab.ui.theme.SkyTrack
import com.amy.daily5vocab.ui.theme.SlateGray
import com.amy.daily5vocab.ui.theme.StreakFlame
import java.util.Locale

/**
 * Detail view for a single vocabulary word, opened by tapping a word on Today or History.
 * Mirrors the design in app/6.png: a header card with the word, its topic, a pronunciation
 * button, and its meaning, plus a "Next Word" button that cycles through that day's set.
 *
 * Fields absent from the app's word data (IPA phonetic, English example, image) are only
 * rendered when supplied, so the card degrades cleanly when they are missing.
 */
@Composable
fun WordDetailScreen(
    word: String,
    topic: String,
    meaning: String,
    phonetic: String? = null,
    example: String? = null,
    streak: Int = 5,
    showNext: Boolean = true,
    onBack: () -> Unit = {},
    onNext: () -> Unit = {},
) {
    val speak = rememberSpeaker()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GrowthSurface),
    ) {
        DetailTopBar(streak = streak, onBack = onBack)
        HorizontalDivider(color = SkyTrack.copy(alpha = 0.6f))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            Spacer(Modifier.height(24.dp))
            WordCard(
                word = word,
                topic = topic,
                meaning = meaning,
                phonetic = phonetic,
                example = example,
                onSpeak = { speak(word) },
            )
            Spacer(Modifier.height(24.dp))
        }

        if (showNext) {
            NextWordButton(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            )
        }
    }
}

@Composable
private fun DetailTopBar(streak: Int, onBack: () -> Unit) {
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
                tint = GrowthGreenDeep,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(Modifier.weight(1f))
        StreakBadge(streak = streak)
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
private fun WordCard(
    word: String,
    topic: String,
    meaning: String,
    phonetic: String?,
    example: String?,
    onSpeak: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GrowthCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TopicChip(topic)
                Spacer(Modifier.weight(1f))
                SpeakerButton(onSpeak)
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = word,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 44.sp,
                color = OxfordBlue,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            if (!phonetic.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = phonetic,
                    fontSize = 16.sp,
                    color = SlateGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = SkyTrack.copy(alpha = 0.7f))
            Spacer(Modifier.height(20.dp))

            SectionLabel("MEANING")
            Spacer(Modifier.height(8.dp))
            Text(
                text = meaning,
                fontSize = 18.sp,
                lineHeight = 26.sp,
                color = OxfordBlue,
            )

            if (!example.isNullOrBlank()) {
                Spacer(Modifier.height(20.dp))
                ExampleBox(example)
            }
        }
    }
}

@Composable
private fun TopicChip(topic: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(GrowthGreen.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            text = topic.uppercase(Locale.getDefault()),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = GrowthGreenDeep,
        )
    }
}

@Composable
private fun SpeakerButton(onSpeak: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(SkyContainer)
            .clickableText(onSpeak),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = "Pronounce word",
            tint = OxfordBlue,
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = SlateGray,
    )
}

@Composable
private fun ExampleBox(example: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SkyContainer.copy(alpha = 0.4f)),
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(72.dp)
                .background(GrowthGreenDeep),
        )
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            SectionLabel("EXAMPLE")
            Spacer(Modifier.height(6.dp))
            Text(
                text = "\"$example\"",
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontStyle = FontStyle.Italic,
                color = SlateGray,
            )
        }
    }
}

@Composable
private fun NextWordButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(GrowthGreenDeep)
            .clickableText(onClick)
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Next Word",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White,
            )
            Spacer(Modifier.size(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

/**
 * Provides a callback that speaks a word aloud via Android [TextToSpeech]. The engine is
 * created for the composition and shut down when it leaves.
 */
@Composable
private fun rememberSpeaker(): (String) -> Unit {
    val context = LocalContext.current
    val tts = remember {
        var engine: TextToSpeech? = null
        engine = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                engine?.language = Locale.US
            }
        }
        engine
    }
    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }
    return { text -> tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, text) }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun WordDetailScreenPreview() {
    Daily5VocabTheme {
        WordDetailScreen(
            word = "Resilient",
            topic = "Business",
            meaning = "Kiên cường",
            phonetic = "/rɪˈzɪl.i.ənt/",
            example = "The community was resilient in the face of the storm.",
        )
    }
}
