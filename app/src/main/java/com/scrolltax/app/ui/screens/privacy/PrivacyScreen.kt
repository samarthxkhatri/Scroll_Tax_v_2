package com.scrolltax.app.ui.screens.privacy

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scrolltax.app.ui.components.*
import com.scrolltax.app.ui.theme.*

@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        FloatingOrb(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset((-60).dp, (40).dp),
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
                    text       = "Privacy Policy",
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

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "ScrollTax is committed to your privacy. We do not sell your data.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(Modifier.height(24.dp))
                    
                    Text(
                        text = "Data Collection",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "We collect usage statistics for Instagram and YouTube locally on your device to help you manage your digital health. This data never leaves your device and is stored in a private database.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(Modifier.height(24.dp))
                    
                    Text(
                        text = "Permissions",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "We use Accessibility Services to detect when you are watching Reels or Shorts. We also use Usage Stats to calculate total time spent in these apps. These permissions are used exclusively for core app functionality and no personal information is transmitted.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = "No Cloud Sync",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Currently, ScrollTax operates 100% offline. There are no servers, and your data stays on the device it was generated on.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                }
                
                Spacer(Modifier.height(40.dp))
                Spacer(Modifier.navigationBarsPadding())
            }
        }
    }
}
