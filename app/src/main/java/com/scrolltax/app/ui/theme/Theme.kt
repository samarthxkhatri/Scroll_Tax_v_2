package com.scrolltax.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val NeonGreen       = Color(0xFF00FF88)
val NeonGreenDark   = Color(0xFF00D46A)
val NeonGreenDim    = Color(0xFF00FF8833)
val Background      = Color(0xFF000000)
val Surface         = Color(0xFF0D0D0D)
val SurfaceVariant  = Color(0xFF141414)
val GlassWhite      = Color(0x14FFFFFF)
val GlassWhiteBorder= Color(0x1FFFFFFF)
val TextPrimary     = Color(0xFFFFFFFF)
val TextSecondary   = Color(0xFF8E8E93)
val TextTertiary    = Color(0xFF48484A)
val ErrorRed        = Color(0xFFFF453A)
val WarnAmber       = Color(0xFFFFD60A)
val CardBg          = Color(0xFF111111)
val DividerColor    = Color(0xFF1C1C1E)

private val ScrollTaxDarkColorScheme = darkColorScheme(
    primary            = NeonGreen,
    onPrimary          = Color(0xFF000000),
    primaryContainer   = Color(0xFF003822),
    onPrimaryContainer = NeonGreen,
    secondary          = NeonGreenDark,
    onSecondary        = Color(0xFF000000),
    background         = Background,
    onBackground       = TextPrimary,
    surface            = Surface,
    onSurface          = TextPrimary,
    surfaceVariant     = SurfaceVariant,
    onSurfaceVariant   = TextSecondary,
    outline            = DividerColor,
    error              = ErrorRed,
    onError            = Color(0xFF000000),
)

@Composable
fun ScrollTaxTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ScrollTaxDarkColorScheme,
        typography  = ScrollTaxTypography,
        content     = content,
    )
}