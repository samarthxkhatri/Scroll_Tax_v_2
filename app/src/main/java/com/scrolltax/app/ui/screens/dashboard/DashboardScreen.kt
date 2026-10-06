package com.scrolltax.app.ui.screens.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.scrolltax.app.data.model.ScrollDebt
import com.scrolltax.app.ui.components.*
import com.scrolltax.app.ui.theme.*
import com.scrolltax.app.util.toHoursMinutesString
import com.scrolltax.app.viewmodel.DashboardUiState
import com.scrolltax.app.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    onNavigateToReports: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        // Ambient background orbs
        FloatingOrb(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(100.dp, (-80).dp),
            size = 300.dp,
        )
        FloatingOrb(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset((-60).dp, 60.dp),
            color    = NeonGreenDark,
            size     = 200.dp,
        )

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top Bar ───────────────────────────────────────────────────────
            DashboardTopBar(
                onSettings = onNavigateToSettings,
                onRefresh  = viewModel::refresh,
            )

            // ── Content ───────────────────────────────────────────────────────
            if (uiState.isLoading) {
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
                    UsageRingsCard(uiState)
                    Spacer(Modifier.height(16.dp))
                    ScrollCountRow(uiState)
                    Spacer(Modifier.height(16.dp))
                    AwarenessCard(uiState.awarenessMessage)
                    Spacer(Modifier.height(16.dp))
                    uiState.scrollDebt?.let { debt ->
                        if (debt.equivalents.isNotEmpty()) {
                            ScrollDebtCard(debt)
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                    WeeklyCard(uiState, onNavigateToReports)
                    Spacer(Modifier.height(16.dp))
                    FooterRow(onPrivacy = onNavigateToPrivacy)
                    Spacer(Modifier.height(32.dp))
                    Spacer(Modifier.navigationBarsPadding())
                }
            }
        }
    }
}

// ── Top Bar ───────────────────────────────────────────────────────────────────

@Composable
private fun DashboardTopBar(
    onSettings: () -> Unit,
    onRefresh: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text       = "ScrollTax",
                style      = MaterialTheme.typography.titleLarge,
                color      = NeonGreen,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text  = "Today's audit",
                style = MaterialTheme.typography.labelMedium,
                color = TextTertiary,
            )
        }
        Row {
            IconButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = TextSecondary)
            }
            IconButton(onClick = onSettings) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextSecondary)
            }
        }
    }
}

// ── Usage Rings Card ──────────────────────────────────────────────────────────

@Composable
private fun UsageRingsCard(state: DashboardUiState) {
    val limitMs = state.dailyLimitMinutes * 60_000L

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        SectionHeader("TODAY'S USAGE")
        Spacer(Modifier.height(24.dp))

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            // Instagram ring
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                NeonProgressRing(
                    progress    = if (limitMs > 0) state.instagramTimeMs.toFloat() / limitMs else 0f,
                    size        = 110.dp,
                    accentColor = NeonGreen,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📸", fontSize = 18.sp)
                        Text(
                            state.instagramTimeMs.toHoursMinutesString(),
                            style      = MaterialTheme.typography.labelMedium,
                            color      = NeonGreen,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Instagram",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                )
            }

            // Combined ring (centre, larger)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                NeonProgressRing(
                    progress    = if (limitMs > 0) state.totalTimeMs.toFloat() / limitMs else 0f,
                    size        = 130.dp,
                    strokeWidth = 10.dp,
                    accentColor = NeonGreen,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            state.totalTimeMs.toHoursMinutesString(),
                            style      = MaterialTheme.typography.titleMedium,
                            color      = NeonGreen,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "total",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary,
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Combined",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                )
            }

            // YouTube ring
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                NeonProgressRing(
                    progress    = if (limitMs > 0) state.youtubeTimeMs.toFloat() / limitMs else 0f,
                    size        = 110.dp,
                    accentColor = NeonGreenDark,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("▶️", fontSize = 18.sp)
                        Text(
                            state.youtubeTimeMs.toHoursMinutesString(),
                            style      = MaterialTheme.typography.labelMedium,
                            color      = NeonGreenDark,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "YouTube",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        UsageBar(
            label   = "Daily limit: ${state.dailyLimitMinutes} min",
            valueMs = state.totalTimeMs,
            maxMs   = limitMs,
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Text(
                text  = "${state.sessionCount} sessions today",
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary,
            )
        }
    }
}

// ── Scroll Count Row ──────────────────────────────────────────────────────────

@Composable
private fun ScrollCountRow(state: DashboardUiState) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        GlassCard(modifier = Modifier.weight(1f)) {
            Text("📸", fontSize = 22.sp)
            Spacer(Modifier.height(8.dp))
            AnimatedCounter(
                count = state.reelCount,
                style = MaterialTheme.typography.headlineMedium,
                color = NeonGreen,
            )
            Text("reels", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }

        GlassCard(modifier = Modifier.weight(1f)) {
            Text("▶️", fontSize = 22.sp)
            Spacer(Modifier.height(8.dp))
            AnimatedCounter(
                count = state.shortCount,
                style = MaterialTheme.typography.headlineMedium,
                color = NeonGreenDark,
            )
            Text("shorts", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }

        GlassCard(modifier = Modifier.weight(1f), accentColor = WarnAmber) {
            Text("🌀", fontSize = 22.sp)
            Spacer(Modifier.height(8.dp))
            AnimatedCounter(
                count = state.reelCount + state.shortCount,
                style = MaterialTheme.typography.headlineMedium,
                color = WarnAmber,
            )
            Text("total", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}

// ── Awareness Card ────────────────────────────────────────────────────────────

@Composable
private fun AwarenessCard(message: String) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Top) {
            Text("💬", fontSize = 20.sp)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    "AWARENESS",
                    style         = MaterialTheme.typography.labelSmall,
                    color         = TextTertiary,
                    letterSpacing = 1.5.sp,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                )
            }
        }
    }
}

// ── Scroll Debt Card ──────────────────────────────────────────────────────────

@Composable
private fun ScrollDebtCard(debt: ScrollDebt) {
    GlassCard(
        modifier    = Modifier.fillMaxWidth(),
        accentColor = ErrorRed.copy(alpha = 0.5f),
    ) {
        SectionHeader(
            title    = "SCROLL DEBT",
            subtitle = "What ${debt.totalReels} reels/shorts cost you",
        )
        Spacer(Modifier.height(16.dp))

        debt.equivalents.forEach { eq ->
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(eq.emoji, fontSize = 18.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        eq.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
                NeonBadge(eq.quantity, color = ErrorRed)
            }
            NeonDivider()
        }
    }
}

// ── Weekly Summary Card ───────────────────────────────────────────────────────

@Composable
private fun WeeklyCard(
    state: DashboardUiState,
    onViewReports: () -> Unit,
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            SectionHeader("WEEKLY TOTAL")
            TextButton(onClick = onViewReports) {
                Text(
                    "View →",
                    color = NeonGreen,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text       = state.weeklyTotalMs.toHoursMinutesString(),
            style      = MaterialTheme.typography.headlineMedium,
            color      = TextPrimary,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text  = "across Instagram and YouTube this week",
            style = MaterialTheme.typography.bodySmall,
            color = TextTertiary,
        )
    }
}

// ── Footer ────────────────────────────────────────────────────────────────────

@Composable
private fun FooterRow(onPrivacy: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .alpha(0.6f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        TextButton(onClick = onPrivacy) {
            Text(
                "Privacy",
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary,
            )
        }
        TextButton(
            onClick = {
                uriHandler.openUri("https://samarthxkhatri.github.io/scrolltax/")
            }
        ) {
            Text(
                "scrolltax.app →",
                style = MaterialTheme.typography.labelSmall,
                color = NeonGreen,
            )
        }
    }
}