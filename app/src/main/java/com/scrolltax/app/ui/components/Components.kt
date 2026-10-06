package com.scrolltax.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.scrolltax.app.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ─────────────────────────────────────────────────────────────────────────────
// GlassCard
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    accentColor: Color = NeonGreen,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(GlassWhite)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.5f),
                        GlassWhiteBorder,
                        accentColor.copy(alpha = 0.1f),
                    )
                ),
                shape = RoundedCornerShape(20.dp),
            )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content  = content,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// NeonProgressRing
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun NeonProgressRing(
    progress: Float,
    size: Dp = 120.dp,
    strokeWidth: Dp = 8.dp,
    accentColor: Color = NeonGreen,
    trackColor: Color = GlassWhite,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val animatedProgress by animateFloatAsState(
        targetValue   = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label         = "progress",
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier         = Modifier.size(size),
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .drawBehind {
                    val strokePx = strokeWidth.toPx()
                    val radius   = (this.size.minDimension - strokePx) / 2f
                    val center   = Offset(this.size.width / 2f, this.size.height / 2f)

                    // Track ring
                    drawCircle(
                        color  = trackColor,
                        radius = radius,
                        center = center,
                        style  = Stroke(strokePx, cap = StrokeCap.Round),
                    )

                    // Progress arc
                    drawArc(
                        brush      = Brush.sweepGradient(
                            0f   to accentColor.copy(alpha = 0.3f),
                            0.5f to accentColor,
                            1f   to accentColor,
                        ),
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter  = false,
                        style      = Stroke(strokePx, cap = StrokeCap.Round),
                    )

                    // Glowing dot at the tip of the arc
                    if (animatedProgress > 0.01f) {
                        val angle = (-90f + 360f * animatedProgress) * (PI / 180f).toFloat()
                        val dotX  = center.x + radius * cos(angle)
                        val dotY  = center.y + radius * sin(angle)
                        drawCircle(
                            color  = accentColor,
                            radius = strokePx / 2f,
                            center = Offset(dotX, dotY),
                        )
                    }
                }
        )
        content()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// AnimatedCounter
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AnimatedCounter(
    count: Int,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.displaySmall,
    color: Color = TextPrimary,
) {
    val animatedCount by animateIntAsState(
        targetValue   = count,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label         = "counter",
    )
    Text(
        text       = animatedCount.toString(),
        style      = style,
        color      = color,
        fontWeight = FontWeight.Bold,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// NeonBadge
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun NeonBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = NeonGreen,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text       = text,
            style      = MaterialTheme.typography.labelSmall,
            color      = color,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SectionHeader
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text          = title,
            style         = MaterialTheme.typography.titleMedium,
            color         = TextSecondary,
            fontWeight    = FontWeight.Medium,
            letterSpacing = 1.5.sp,
        )
        if (subtitle != null) {
            Spacer(Modifier.height(2.dp))
            Text(
                text  = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// NeonDivider
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun NeonDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier  = modifier,
        thickness = 1.dp,
        color     = DividerColor,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// FloatingOrb  —  decorative ambient background element
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun FloatingOrb(
    modifier: Modifier = Modifier,
    color: Color = NeonGreen,
    size: Dp = 200.dp,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb")
    val alpha by infiniteTransition.animateFloat(
        initialValue  = 0.03f,
        targetValue   = 0.08f,
        animationSpec = infiniteRepeatable(
            animation  = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "orb_alpha",
    )

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(color.copy(alpha = alpha), Color.Transparent)
                )
            )
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// UsageBar  —  horizontal progress bar with animated fill
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun UsageBar(
    label: String,
    valueMs: Long,
    maxMs: Long,
    color: Color = NeonGreen,
    modifier: Modifier = Modifier,
) {
    val progress by animateFloatAsState(
        targetValue   = if (maxMs > 0) (valueMs.toFloat() / maxMs).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label         = "bar_progress",
    )

    Column(modifier = modifier) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
            Text(
                "${(valueMs / 60_000)} min",
                style      = MaterialTheme.typography.bodySmall,
                color      = color,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(GlassWhite)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(color.copy(alpha = 0.7f), color)
                        )
                    )
            )
        }
    }
}