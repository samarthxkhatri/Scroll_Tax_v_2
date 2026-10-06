package com.scrolltax.app.data.model

data class UserPreferences(
    val dailyLimitMinutes: Int          = 60,
    val avgReelDurationSeconds: Int     = 30,
    val notificationsEnabled: Boolean   = true,
    val accessibilityTrackingEnabled: Boolean = true,
    val onboardingComplete: Boolean     = false,
    val usagePermissionGranted: Boolean = false,
    val accessibilityPermissionGranted: Boolean = false,
    val notificationPermissionGranted: Boolean  = false,
)