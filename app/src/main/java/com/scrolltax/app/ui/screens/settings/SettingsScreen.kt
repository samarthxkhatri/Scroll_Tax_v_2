package com.scrolltax.app.ui.screens.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scrolltax.app.ui.components.*
import com.scrolltax.app.ui.theme.*
import com.scrolltax.app.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        FloatingOrb(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset((60).dp, (-40).dp),
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
                    text       = "Settings",
                    style      = MaterialTheme.typography.titleLarge,
                    color      = TextPrimary,
                    fontWeight = FontWeight.Bold,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            ) {
                Spacer(Modifier.height(16.dp))

                SectionHeader(title = "LIMITS", subtitle = "Control your daily scroll quota")
                Spacer(Modifier.height(16.dp))
                
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    SettingsSliderItem(
                        label = "Daily Limit",
                        value = uiState.dailyLimitMinutes,
                        onValueChange = { viewModel.setDailyLimit(it.toInt()) },
                        valueRange = 5f..180f,
                        unit = "min"
                    )
                    
                    NeonDivider(Modifier.padding(vertical = 16.dp))
                    
                    SettingsSliderItem(
                        label = "Avg. Reel Duration",
                        value = uiState.avgReelDurationSeconds,
                        onValueChange = { viewModel.setAvgReelDuration(it.toInt()) },
                        valueRange = 15f..60f,
                        unit = "sec"
                    )
                }

                Spacer(Modifier.height(32.dp))
                SectionHeader(title = "PERMISSIONS", subtitle = "Enable tracking and alerts")
                Spacer(Modifier.height(16.dp))

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    SettingsSwitchItem(
                        label = "Notifications",
                        enabled = uiState.notificationsEnabled,
                        onCheckedChange = { viewModel.setNotificationsEnabled(it) },
                        icon = Icons.Default.Notifications
                    )
                    
                    NeonDivider(Modifier.padding(vertical = 16.dp))
                    
                    SettingsSwitchItem(
                        label = "Accessibility Tracking",
                        enabled = uiState.accessibilityEnabled,
                        onCheckedChange = { viewModel.setAccessibilityEnabled(it) },
                        icon = Icons.Default.Visibility
                    )
                }

                Spacer(Modifier.height(32.dp))
                
                TextButton(
                    onClick = onNavigateToPrivacy,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Privacy Policy", color = NeonGreen, fontWeight = FontWeight.SemiBold)
                }
                
                Spacer(Modifier.height(40.dp))
                Spacer(Modifier.navigationBarsPadding())
            }
        }
    }
}

@Composable
private fun SettingsSliderItem(
    label: String,
    value: Int,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    unit: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
            Text("$value $unit", style = MaterialTheme.typography.bodyLarge, color = NeonGreen, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        Slider(
            value = value.toFloat(),
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = NeonGreen,
                activeTrackColor = NeonGreen,
                inactiveTrackColor = GlassWhite
            )
        )
    }
}

@Composable
private fun SettingsSwitchItem(
    label: String,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(12.dp))
            Text(label, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
        }
        Switch(
            checked = enabled,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NeonGreen,
                checkedTrackColor = NeonGreen.copy(alpha = 0.5f),
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = SurfaceVariant
            )
        )
    }
}
