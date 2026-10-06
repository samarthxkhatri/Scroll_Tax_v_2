package com.scrolltax.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.scrolltax.app.data.repository.PreferencesRepository
import com.scrolltax.app.data.repository.UsageRepository
import com.scrolltax.app.notification.NotificationSender
import com.scrolltax.app.util.todayEpochDay
import com.scrolltax.app.util.toMinutesFloat
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

const val WORK_TAG_SYNC = "scrolltax_sync"

@HiltWorker
class UsageSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val usageRepository: UsageRepository,
    private val preferencesRepository: PreferencesRepository,
    private val notificationSender: NotificationSender,
) : CoroutineWorker(context, params) {

    companion object {
        private var notified30 = false; private var notified60 = false
        private var notified90 = false; private var notifiedLim = false
        private var lastNotifyDay = -1L
    }

    override suspend fun doWork(): Result {
        return try {
            usageRepository.syncUsageStats()
            usageRepository.syncSessionsFromEvents(System.currentTimeMillis() - 20 * 60 * 1000L)
            checkThresholds()
            usageRepository.pruneOldData()
            Result.success()
        } catch (e: Exception) { Result.retry() }
    }

    private suspend fun checkThresholds() {
        val prefs = preferencesRepository.userPreferences.first()
        if (!prefs.notificationsEnabled) return
        val today   = todayEpochDay()
        val records = usageRepository.observeTodayUsageRecords().first()
        val counts  = usageRepository.observeTodayContentCounts().first()
        if (lastNotifyDay != today) {
            notified30 = false; notified60 = false; notified90 = false; notifiedLim = false
            lastNotifyDay = today
        }
        val totalMs      = records.sumOf { it.totalTimeMs }
        val totalMinutes = totalMs.toMinutesFloat().toInt()
        val reelCount    = counts?.reelCount  ?: 0
        val shortCount   = counts?.shortCount ?: 0
        if (totalMinutes >= 30  && !notified30)  { notificationSender.sendThirtyMinuteAlert(reelCount); notified30 = true }
        if (totalMinutes >= 60  && !notified60)  { notificationSender.sendOneHourAlert(reelCount, totalMinutes); notified60 = true }
        if (totalMinutes >= 90  && !notified90)  { notificationSender.sendNinetyMinuteAlert(reelCount + shortCount); notified90 = true }
        if (totalMinutes >= prefs.dailyLimitMinutes && !notifiedLim) {
            notificationSender.sendLimitExceededAlert(prefs.dailyLimitMinutes, totalMinutes); notifiedLim = true
        }
    }
}

@Singleton
class WorkerScheduler @Inject constructor(private val context: Context) {
    fun schedulePeriodicSync() {
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_TAG_SYNC,
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<UsageSyncWorker>(15, TimeUnit.MINUTES, 5, TimeUnit.MINUTES)
                .addTag(WORK_TAG_SYNC).build()
        )
    }
    fun scheduleImmediateSync() {
        WorkManager.getInstance(context).enqueue(
            OneTimeWorkRequestBuilder<UsageSyncWorker>().addTag(WORK_TAG_SYNC).build()
        )
    }
}