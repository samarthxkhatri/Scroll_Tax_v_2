package com.scrolltax.app.ui.screens.reports

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.scrolltax.app.data.model.DailySummary
import com.scrolltax.app.ui.components.*
import com.scrolltax.app.ui.theme.*
import com.scrolltax.app.util.toHoursMinutesString
import com.scrolltax.app.util.toReadableDate
import com.scrolltax.app.viewmodel.ReportsUiState
import com.scrolltax.app.viewmodel.ReportsViewModel

@Composable
fun ReportsScreen(
    onBack: () -> Unit,
    viewModel: ReportsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        FloatingOrb(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset((-60).dp, (-40).dp),
        )

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top Bar ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint               = TextSecondary,
                    )
                }
                Text(
                    text       = "Weekly Report",
                    style      = MaterialTheme.typography.titleLarge,
                    color      = TextPrimary,
                    fontWeight = FontWeight.Bold,
                )
            }

            if (state.isLoading) {
                Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = NeonGreen)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
                ) {
                    Spacer(Modifier.height(8.dp))

                    SummaryStatsRow(state)

                    Spacer(Modifier.height(20.dp))

                    // Daily usage time chart
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        SectionHeader("DAILY USAGE", "Last 7 days")
                        Spacer(Modifier.height(20.dp))
                        WeeklyBarChart(
                            summaries   = state.weeklySummaries,
                            accentColor = NeonGreen,
                            valueLabel  = { it.totalTimeMs.toHoursMinutesString() },
                            barValue    = { it.totalTimeMs.toFloat() },
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Instagram reels chart
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        SectionHeader("REELS WATCHED", "Instagram daily count")
                        Spacer(Modifier.height(20.dp))
                        WeeklyBarChart(
                            summaries   = state.weeklySummaries,
                            accentColor = NeonGreenDark,
                            valueLabel  = { "${it.reelCount}" },
                            barValue    = { it.reelCount.toFloat() },
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // YouTube shorts chart
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        SectionHeader("SHORTS WATCHED", "YouTube daily count")
                        Spacer(Modifier.height(20.dp))
                        WeeklyBarChart(
                            summaries   = state.weeklySummaries,
                            accentColor = WarnAmber,
                            valueLabel  = { "${it.shortCount}" },
                            barValue    = { it.shortCount.toFloat() },
                        )
                    }

                    Spacer(Modifier.height(32.dp))
                    Spacer(Modifier.navigationBarsPadding())
                }
            }
        }
    }
}

// ── Summary Stats Row ─────────────────────────────────────────────────────────

@Composable
private fun SummaryStatsRow(state: ReportsUiState) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        MiniStatCard(
            modifier = Modifier.weight(1f),
            emoji    = "⏱",
            value    = "${state.totalWeeklyMinutes / 60}h ${state.totalWeeklyMinutes % 60}m",
            label    = "This week",
            color    = TextPrimary,
        )
        MiniStatCard(
            modifier = Modifier.weight(1f),
            emoji    = "📸",
            value    = "${state.totalWeeklyReels}",
            label    = "Reels",
            color    = NeonGreen,
        )
        MiniStatCard(
            modifier = Modifier.weight(1f),
            emoji    = "▶️",
            value    = "${state.totalWeeklyShorts}",
            label    = "Shorts",
            color    = WarnAmber,
        )
    }
}

@Composable
private fun MiniStatCard(
    modifier: Modifier = Modifier,
    emoji: String,
    value: String,
    label: String,
    color: Color = TextPrimary,
) {
    GlassCard(
        modifier    = modifier,
        accentColor = color.copy(alpha = 0.3f),
    ) {
        Text(emoji, fontSize = 20.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            text       = value,
            style      = MaterialTheme.typography.titleLarge,
            color      = color,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextTertiary,
        )
    }
}

// ── Bar Chart ─────────────────────────────────────────────────────────────────

@Composable
private fun WeeklyBarChart(
    summaries: List<DailySummary>,
    accentColor: Color,
    valueLabel: (DailySummary) -> String,
    barValue: (DailySummary) -> Float,
) {
    if (summaries.isEmpty()) {
        Box(
            modifier         = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "No data yet",
                color = TextTertiary,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        return
    }

    val maxVal    = summaries.maxOfOrNull(barValue) ?: 1f
    val barHeight = 120.dp

    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment     = Alignment.Bottom,
    ) {
        summaries.forEach { summary ->
            val targetFraction  = if (maxVal > 0) barValue(summary) / maxVal else 0f
            val animatedFraction by animateFloatAsState(
                targetValue   = targetFraction,
                animationSpec = tween(1000, easing = FastOutSlowInEasing),
                label         = "bar_${summary.epochDay}",
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier            = Modifier.weight(1f),
            ) {
                // Value label above tall bars
                if (targetFraction > 0.6f) {
                    Text(
                        text       = valueLabel(summary),
                        style      = MaterialTheme.typography.labelSmall,
                        color      = accentColor,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(4.dp))
                }

                // The bar itself
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(barHeight * animatedFraction.coerceAtLeast(0.04f))
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(
                            if (targetFraction > 0.01f)
                                Brush.verticalGradient(
                                    listOf(accentColor, accentColor.copy(alpha = 0.4f))
                                )
                            else
                                Brush.verticalGradient(
                                    listOf(GlassWhite, GlassWhite)
                                )
                        )
                )

                Spacer(Modifier.height(6.dp))

                // Day label  e.g. "Mon"
                Text(
                    text      = summary.epochDay.toReadableDate().take(3),
                    style     = MaterialTheme.typography.labelSmall,
                    color     = TextTertiary,
                    fontSize  = 9.sp,
                )
            }
        }
    }
}