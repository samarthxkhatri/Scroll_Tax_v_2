package com.scrolltax.app.ui.screens.welcome

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scrolltax.app.ui.components.FloatingOrb
import com.scrolltax.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(onContinue: () -> Unit) {

    // ── Staggered entrance animations ────────────────────────────────────────
    val logoAlpha    by produceAnimatedFloat(delayMs = 200,  from = 0f,   to = 1f)
    val logoScale    by produceAnimatedFloat(delayMs = 200,  from = 0.7f, to = 1f)
    val taglineAlpha by produceAnimatedFloat(delayMs = 800,  from = 0f,   to = 1f)
    val ctaAlpha     by produceAnimatedFloat(delayMs = 1400, from = 0f,   to = 1f)

    // ── Pulsing logo dot ──────────────────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.12f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse_scale",
    )

    // Auto-advance after 4 seconds if user doesn't tap
    LaunchedEffect(Unit) {
        delay(4000L)
        onContinue()
    }

    val uriHandler = LocalUriHandler.current

    Box(
        modifier         = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center,
    ) {

        // ── Ambient background orbs ───────────────────────────────────────────
        FloatingOrb(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-30).dp),
            color    = NeonGreen,
            size     = 280.dp,
        )
        FloatingOrb(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-40).dp, y = 40.dp),
            color    = NeonGreenDark,
            size     = 200.dp,
        )

        // ── Main content column ───────────────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier            = Modifier
                .padding(horizontal = 40.dp),
        ) {

            // Logo monogram
            Box(
                contentAlignment = Alignment.Center,
                modifier         = Modifier
                    .scale(pulseScale * logoScale)
                    .alpha(logoAlpha)
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                NeonGreen.copy(alpha = 0.2f),
                                NeonGreen.copy(alpha = 0.05f),
                            )
                        )
                    )
            ) {
                Text(
                    text       = "ST",
                    style      = MaterialTheme.typography.headlineLarge,
                    color      = NeonGreen,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.height(32.dp))

            // App name
            Text(
                text       = "ScrollTax",
                style      = MaterialTheme.typography.displaySmall,
                color      = TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier
                    .alpha(logoAlpha)
                    .scale(logoScale),
            )

            Spacer(Modifier.height(12.dp))

            // Tagline
            Text(
                text      = "Every reel costs more than you think.",
                style     = MaterialTheme.typography.bodyLarge,
                color     = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 26.sp,
                modifier  = Modifier.alpha(taglineAlpha),
            )

            Spacer(Modifier.height(64.dp))

            // CTA button
            Button(
                onClick  = onContinue,
                modifier = Modifier
                    .alpha(ctaAlpha)
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreen,
                    contentColor   = Background,
                ),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text(
                    text       = "Start your audit",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text     = "No account. No data shared. Ever.",
                style    = MaterialTheme.typography.labelSmall,
                color    = TextTertiary,
                modifier = Modifier.alpha(ctaAlpha),
            )
        }

        // ── Footer website link ───────────────────────────────────────────────
        TextButton(
            onClick  = { uriHandler.openUri("https://samarthxkhatri.github.io/scrolltax/") },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .alpha(ctaAlpha)
                .padding(bottom = 32.dp)
                .navigationBarsPadding(),
        ) {
            Text(
                text  = "scrolltax.app →",
                style = MaterialTheme.typography.labelSmall,
                color = NeonGreen,
            )
        }
    }
}

// ── Animation helper ──────────────────────────────────────────────────────────

@Composable
private fun produceAnimatedFloat(
    delayMs: Int,
    from: Float,
    to: Float,
): State<Float> {
    val animatable = remember { Animatable(from) }
    LaunchedEffect(Unit) {
        delay(delayMs.toLong())
        animatable.animateTo(
            targetValue   = to,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        )
    }
    return animatable.asState()
}