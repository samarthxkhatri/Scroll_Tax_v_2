package com.scrolltax.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.scrolltax.app.data.model.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "scrolltax_prefs")

@Singleton
class PreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val DAILY_LIMIT_MINUTES       = intPreferencesKey("daily_limit_minutes")
        val AVG_REEL_DURATION_SECONDS = intPreferencesKey("avg_reel_duration_seconds")
        val NOTIFICATIONS_ENABLED     = booleanPreferencesKey("notifications_enabled")
        val ACCESSIBILITY_TRACKING    = booleanPreferencesKey("accessibility_tracking_enabled")
        val ONBOARDING_COMPLETE       = booleanPreferencesKey("onboarding_complete")
        val USAGE_PERMISSION_GRANTED  = booleanPreferencesKey("usage_permission_granted")
        val ACCESSIBILITY_PERMISSION  = booleanPreferencesKey("accessibility_permission_granted")
        val NOTIFICATION_PERMISSION   = booleanPreferencesKey("notification_permission_granted")
    }

    val userPreferences: Flow<UserPreferences> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs ->
            UserPreferences(
                dailyLimitMinutes             = prefs[Keys.DAILY_LIMIT_MINUTES]       ?: 60,
                avgReelDurationSeconds        = prefs[Keys.AVG_REEL_DURATION_SECONDS] ?: 30,
                notificationsEnabled          = prefs[Keys.NOTIFICATIONS_ENABLED]     ?: true,
                accessibilityTrackingEnabled  = prefs[Keys.ACCESSIBILITY_TRACKING]    ?: true,
                onboardingComplete            = prefs[Keys.ONBOARDING_COMPLETE]       ?: false,
                usagePermissionGranted        = prefs[Keys.USAGE_PERMISSION_GRANTED]  ?: false,
                accessibilityPermissionGranted = prefs[Keys.ACCESSIBILITY_PERMISSION] ?: false,
                notificationPermissionGranted  = prefs[Keys.NOTIFICATION_PERMISSION]  ?: false,
            )
        }

    suspend fun setDailyLimitMinutes(minutes: Int) = context.dataStore.edit { it[Keys.DAILY_LIMIT_MINUTES] = minutes }
    suspend fun setAvgReelDurationSeconds(seconds: Int) = context.dataStore.edit { it[Keys.AVG_REEL_DURATION_SECONDS] = seconds }
    suspend fun setNotificationsEnabled(enabled: Boolean) = context.dataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled }
    suspend fun setAccessibilityTracking(enabled: Boolean) = context.dataStore.edit { it[Keys.ACCESSIBILITY_TRACKING] = enabled }
    suspend fun setOnboardingComplete(complete: Boolean) = context.dataStore.edit { it[Keys.ONBOARDING_COMPLETE] = complete }
    suspend fun setUsagePermissionGranted(granted: Boolean) = context.dataStore.edit { it[Keys.USAGE_PERMISSION_GRANTED] = granted }
    suspend fun setAccessibilityPermissionGranted(granted: Boolean) = context.dataStore.edit { it[Keys.ACCESSIBILITY_PERMISSION] = granted }
    suspend fun setNotificationPermissionGranted(granted: Boolean) = context.dataStore.edit { it[Keys.NOTIFICATION_PERMISSION] = granted }
}