package com.scrolltax.app.ui.screens.onboarding

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scrolltax.app.ui.components.*
import com.scrolltax.app.ui.theme.*
import com.scrolltax.app.util.UsageStatsHelper
import com.scrolltax.app.viewmodel.OnboardingViewModel
import kotlinx.coroutines.launch

// ── Page data ─────────────────────────────────────────────────────────────────

private data class OnboardingPage(
    val emoji: String,
    val headline: String,
    val subtext: String,
    val accentLabel: String? = null,
)

private val pages = listOf(
    OnboardingPage(
        emoji       = "🧠",
        headline    = "Your attention is your\nmost valuable asset.",
        subtext     = "Every second of focus you give is time you can never reclaim. " +
                "Instagram and YouTube are optimised to take as much of it as possible.",
        accentLabel = "Awareness is power",
    ),
    OnboardingPage(
        emoji       = "🎯",
        headline    = "Instagram hides\nthe cost.",
        subtext     = "There are no timestamps. No session summaries. No reminders. " +
                "By design — because knowing would change your behaviour.",
        accentLabel = "Hidden by design",
    ),
    OnboardingPage(
        emoji       = "💡",
        headline    = "ScrollTax\nreveals it.",
        subtext     = "We track the time you spend, count the reels you watch, and show you " +
                "what you could have built instead. All of it stays on your device.",
        accentLabel = "100% private",
    ),
    OnboardingPage(
        emoji       = "🔐",
        headline    = "Set up\npermissions.",
        subtext     = "ScrollTax needs 3 permissions — nothing else. " +
                "We will explain each one before asking for it.",
        accentLabel = "Only what's necessary",
    ),
)

// ── Screen ────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val context    = LocalContext.current
    val pagerState = rememberPagerState { pages.size }
    val scope      = rememberCoroutineScope()
    val usageHelper = remember { UsageStatsHelper(context) }

    var usageGranted by remember { mutableStateOf(usageHelper.hasUsageStatsPermission()) }
    var a11yGranted  by remember { mutableStateOf(isAccessibilityEnabled(context)) }
    var notifGranted by remember { mutableStateOf(isNotifPermissionGranted(context)) }

    val notifLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        notifGranted = granted
        viewModel.setNotificationPermissionGranted(granted)
    }

    // Re-check permissions when pager lands on page 3
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage == 3) {
            usageGranted = usageHelper.hasUsageStatsPermission()
            a11yGranted  = isAccessibilityEnabled(context)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        FloatingOrb(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(80.dp, (-60).dp),
            size = 260.dp,
        )

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Pager ─────────────────────────────────────────────────────────
            HorizontalPager(
                state    = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                if (page < 3) {
                    InfoPage(pages[page])
                } else {
                    PermissionsPage(
                        usageGranted   = usageGranted,
                        a11yGranted    = a11yGranted,
                        notifGranted   = notifGranted,
                        onRequestUsage = {
                            context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                        },
                        onRequestA11y = {
                            context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                        },
                        onRequestNotif = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notifLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                notifGranted = true
                            }
                        },
                    )
                }
            }

            // ── Page dot indicators ───────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(pages.size) { idx ->
                    val selected = pagerState.currentPage == idx
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(CircleShape)
                            .background(if (selected) NeonGreen else TextTertiary)
                            .size(
                                width  = if (selected) 24.dp else 8.dp,
                                height = 8.dp,
                            )
                    )
                }
            }

            // ── Navigation buttons ────────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                if (pagerState.currentPage > 0) {
                    TextButton(onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }) {
                        Text("Back", color = TextSecondary)
                    }
                } else {
                    TextButton(onClick = {
                        viewModel.completeOnboarding()
                        onFinish()
                    }) {
                        Text("Skip", color = TextTertiary)
                    }
                }

                Button(
                    onClick = {
                        if (pagerState.currentPage < pages.size - 1) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            viewModel.setUsagePermissionGranted(usageGranted)
                            viewModel.setAccessibilityPermissionGranted(a11yGranted)
                            viewModel.setNotificationPermissionGranted(notifGranted)
                            viewModel.completeOnboarding()
                            onFinish()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen,
                        contentColor   = Background,
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text       = if (pagerState.currentPage < pages.size - 1) "Next →" else "Start tracking",
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(Modifier.navigationBarsPadding())
        }
    }
}

// ── Info Page (pages 0-2) ─────────────────────────────────────────────────────

@Composable
private fun InfoPage(page: OnboardingPage) {
    Column(
        modifier            = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(page.emoji, style = MaterialTheme.typography.displaySmall)

        Spacer(Modifier.height(24.dp))

        page.accentLabel?.let { label ->
            NeonBadge(text = label)
            Spacer(Modifier.height(16.dp))
        }

        Text(
            text       = page.headline,
            style      = MaterialTheme.typography.headlineMedium,
            color      = TextPrimary,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text  = page.subtext,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
        )
    }
}

// ── Permissions Page (page 3) ─────────────────────────────────────────────────

@Composable
private fun PermissionsPage(
    usageGranted: Boolean,
    a11yGranted: Boolean,
    notifGranted: Boolean,
    onRequestUsage: () -> Unit,
    onRequestA11y: () -> Unit,
    onRequestNotif: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 40.dp),
    ) {
        Text("🔐", style = MaterialTheme.typography.displaySmall)
        Spacer(Modifier.height(16.dp))
        NeonBadge("Only what's necessary")
        Spacer(Modifier.height(16.dp))

        Text(
            text       = "Set up\npermissions.",
            style      = MaterialTheme.typography.headlineMedium,
            color      = TextPrimary,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text  = "ScrollTax uses exactly 3 permissions — nothing else. " +
                    "Your messages, contacts, and personal data are never accessed.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )

        Spacer(Modifier.height(32.dp))

        PermissionItem(
            icon        = "📊",
            title       = "Usage Access",
            description = "Measures time spent on Instagram and YouTube. " +
                    "ScrollTax never reads what you watch — only how long you watch it.",
            isGranted   = usageGranted,
            onRequest   = onRequestUsage,
        )

        Spacer(Modifier.height(16.dp))

        PermissionItem(
            icon        = "♿",
            title       = "Accessibility Service",
            description = "Detects when you swipe to a new reel or short. " +
                    "ScrollTax does NOT read messages, passwords, or any content. " +
                    "It only counts swipe transitions.",
            isGranted   = a11yGranted,
            onRequest   = onRequestA11y,
        )

        Spacer(Modifier.height(16.dp))

        PermissionItem(
            icon        = "🔔",
            title       = "Notifications",
            description = "Sends awareness reminders at 30, 60, and 90-minute milestones. " +
                    "No spam. No marketing. You control the limits.",
            isGranted   = notifGranted,
            onRequest   = onRequestNotif,
        )
    }
}

// ── Single permission row card ────────────────────────────────────────────────

@Composable
private fun PermissionItem(
    icon: String,
    title: String,
    description: String,
    isGranted: Boolean,
    onRequest: () -> Unit,
) {
    GlassCard(
        accentColor = if (isGranted) NeonGreen else GlassWhiteBorder,
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Text(icon, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    Text(
                        text       = title,
                        style      = MaterialTheme.typography.titleSmall,
                        color      = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (isGranted) NeonBadge("✓ Granted")
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text  = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
                if (!isGranted) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onRequest,
                        colors  = ButtonDefaults.outlinedButtonColors(contentColor = NeonGreen),
                        border  = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f)),
                        shape   = RoundedCornerShape(8.dp),
                    ) {
                        Text("Enable", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// ── Permission check helpers ──────────────────────────────────────────────────

private fun isAccessibilityEnabled(context: android.content.Context): Boolean {
    val service = "${context.packageName}/.accessibility.ScrollTaxAccessibilityService"
    val enabled = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
    ) ?: return false
    return enabled.contains(service)
}

private fun isNotifPermissionGranted(context: android.content.Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
    } else true
}