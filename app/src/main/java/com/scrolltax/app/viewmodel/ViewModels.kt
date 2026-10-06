package com.scrolltax.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrolltax.app.data.model.*
import com.scrolltax.app.data.repository.PreferencesRepository
import com.scrolltax.app.data.repository.UsageRepository
import com.scrolltax.app.util.*
import com.scrolltax.app.worker.WorkerScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean          = true,
    val instagramTimeMs: Long       = 0L,
    val youtubeTimeMs: Long         = 0L,
    val reelCount: Int              = 0,
    val shortCount: Int             = 0,
    val sessionCount: Int           = 0,
    val totalTimeMs: Long           = 0L,
    val scrollDebt: ScrollDebt?     = null,
    val awarenessMessage: String    = "",
    val dailyLimitMinutes: Int      = 60,
    val weeklyTotalMs: Long         = 0L,
    val preferences: UserPreferences = UserPreferences(),
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val usageRepository: UsageRepository,
    private val preferencesRepository: PreferencesRepository,
    private val scrollDebtEngine: ScrollDebtEngine,
    private val workerScheduler: WorkerScheduler,
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        usageRepository.observeTodayUsageRecords(),
        usageRepository.observeTodayContentCounts(),
        usageRepository.observeTodaySessionCount(),
        usageRepository.observeWeeklySummaries(),
        preferencesRepository.userPreferences,
    ) { records, counts, sessionCount, weeklySummaries, prefs ->
        val igTime     = records.find { it.packageName == PACKAGE_INSTAGRAM }?.totalTimeMs ?: 0L
        val ytTime     = records.find { it.packageName == PACKAGE_YOUTUBE   }?.totalTimeMs ?: 0L
        val reelCount  = counts?.reelCount  ?: 0
        val shortCount = counts?.shortCount ?: 0
        DashboardUiState(
            isLoading        = false,
            instagramTimeMs  = igTime,
            youtubeTimeMs    = ytTime,
            reelCount        = reelCount,
            shortCount       = shortCount,
            sessionCount     = sessionCount,
            totalTimeMs      = igTime + ytTime,
            scrollDebt       = scrollDebtEngine.compute(reelCount + shortCount, prefs.avgReelDurationSeconds),
            awarenessMessage = scrollDebtEngine.getAwarenessMessage(reelCount + shortCount, prefs.avgReelDurationSeconds),
            dailyLimitMinutes = prefs.dailyLimitMinutes,
            weeklyTotalMs    = weeklySummaries.sumOf { it.totalTimeMs },
            preferences      = prefs,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    init { viewModelScope.launch { workerScheduler.scheduleImmediateSync() } }
    fun refresh() = viewModelScope.launch { workerScheduler.scheduleImmediateSync() }
}

data class ReportsUiState(
    val isLoading: Boolean = true,
    val weeklySummaries: List<DailySummary> = emptyList(),
    val weeklyContentCounts: List<ContentCountRecord> = emptyList(),
    val totalWeeklyReels: Int = 0,
    val totalWeeklyShorts: Int = 0,
    val totalWeeklyMinutes: Int = 0,
    val peakDayMs: Long = 0L,
    val avgDailyMinutes: Int = 0,
)

@HiltViewModel
class ReportsViewModel @Inject constructor(private val usageRepository: UsageRepository) : ViewModel() {
    val uiState: StateFlow<ReportsUiState> = combine(
        usageRepository.observeWeeklySummaries(),
        usageRepository.observeWeeklyContentCounts(),
    ) { summaries, counts ->
        ReportsUiState(
            isLoading           = false,
            weeklySummaries     = summaries,
            weeklyContentCounts = counts,
            totalWeeklyReels    = counts.sumOf { it.reelCount },
            totalWeeklyShorts   = counts.sumOf { it.shortCount },
            totalWeeklyMinutes  = (summaries.sumOf { it.totalTimeMs } / 60_000).toInt(),
            peakDayMs           = summaries.maxOfOrNull { it.totalTimeMs } ?: 0L,
            avgDailyMinutes     = if (summaries.isNotEmpty()) (summaries.sumOf { it.totalTimeMs } / summaries.size / 60_000).toInt() else 0,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ReportsUiState())
}

data class SettingsUiState(
    val dailyLimitMinutes: Int        = 60,
    val avgReelDurationSeconds: Int   = 30,
    val notificationsEnabled: Boolean = true,
    val accessibilityEnabled: Boolean = true,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(private val preferencesRepository: PreferencesRepository) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = preferencesRepository.userPreferences.map {
        SettingsUiState(it.dailyLimitMinutes, it.avgReelDurationSeconds, it.notificationsEnabled, it.accessibilityTrackingEnabled)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun setDailyLimit(minutes: Int) = viewModelScope.launch { preferencesRepository.setDailyLimitMinutes(minutes) }
    fun setAvgReelDuration(seconds: Int) = viewModelScope.launch { preferencesRepository.setAvgReelDurationSeconds(seconds) }
    fun setNotificationsEnabled(enabled: Boolean) = viewModelScope.launch { preferencesRepository.setNotificationsEnabled(enabled) }
    fun setAccessibilityEnabled(enabled: Boolean) = viewModelScope.launch { preferencesRepository.setAccessibilityTracking(enabled) }
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(private val preferencesRepository: PreferencesRepository) : ViewModel() {
    val isOnboardingComplete: StateFlow<Boolean> = preferencesRepository.userPreferences
        .map { it.onboardingComplete }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun completeOnboarding() = viewModelScope.launch { preferencesRepository.setOnboardingComplete(true) }
    fun setUsagePermissionGranted(granted: Boolean) = viewModelScope.launch { preferencesRepository.setUsagePermissionGranted(granted) }
    fun setAccessibilityPermissionGranted(granted: Boolean) = viewModelScope.launch { preferencesRepository.setAccessibilityPermissionGranted(granted) }
    fun setNotificationPermissionGranted(granted: Boolean) = viewModelScope.launch { preferencesRepository.setNotificationPermissionGranted(granted) }
}