package com.scrolltax.app.data.repository

import android.app.usage.UsageEvents
import com.scrolltax.app.data.dao.*
import com.scrolltax.app.data.model.*
import com.scrolltax.app.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsageRepository @Inject constructor(
    private val usageRecordDao: UsageRecordDao,
    private val sessionRecordDao: SessionRecordDao,
    private val contentCountRecordDao: ContentCountRecordDao,
    private val dailySummaryDao: DailySummaryDao,
    private val usageStatsHelper: UsageStatsHelper,
) {
    fun observeTodayContentCounts(): Flow<ContentCountRecord?> =
        contentCountRecordDao.observeForDay(todayEpochDay())

    fun observeTodayUsageRecords(): Flow<List<UsageRecord>> =
        usageRecordDao.observeForDay(todayEpochDay())

    fun observeTodaySessions(): Flow<List<SessionRecord>> =
        sessionRecordDao.observeSessionsForDay(todayEpochDay())

    fun observeTodaySessionCount(): Flow<Int> =
        sessionRecordDao.observeSessionCountForDay(todayEpochDay())

    fun observeWeeklySummaries(): Flow<List<DailySummary>> =
        dailySummaryDao.observeSince(daysAgoEpochDay(6))

    fun observeWeeklyContentCounts(): Flow<List<ContentCountRecord>> =
        contentCountRecordDao.observeSince(daysAgoEpochDay(6))

    suspend fun syncUsageStats() = withContext(Dispatchers.IO) {
        val today  = todayEpochDay()
        val igTime = usageStatsHelper.getInstagramTimeToday()
        val ytTime = usageStatsHelper.getYouTubeTimeToday()
        usageRecordDao.upsert(UsageRecord(epochDay = today, packageName = PACKAGE_INSTAGRAM, totalTimeMs = igTime))
        usageRecordDao.upsert(UsageRecord(epochDay = today, packageName = PACKAGE_YOUTUBE,   totalTimeMs = ytTime))
        val counts = contentCountRecordDao.getForDay(today)
        dailySummaryDao.upsert(DailySummary(
            epochDay        = today,
            instagramTimeMs = igTime,
            youtubeTimeMs   = ytTime,
            reelCount       = counts?.reelCount  ?: 0,
            shortCount      = counts?.shortCount ?: 0,
        ))
    }

    suspend fun openSession(packageName: String) = withContext(Dispatchers.IO) {
        if (sessionRecordDao.getOpenSession(packageName) == null) {
            sessionRecordDao.insert(SessionRecord(
                packageName = packageName,
                startTime   = System.currentTimeMillis(),
                endTime     = null,
                epochDay    = todayEpochDay(),
            ))
        }
    }

    suspend fun closeSession(packageName: String) = withContext(Dispatchers.IO) {
        val session = sessionRecordDao.getOpenSession(packageName) ?: return@withContext
        val endTime = System.currentTimeMillis()
        sessionRecordDao.closeSession(session.id, endTime, endTime - session.startTime)
    }

    suspend fun incrementReelCount() = withContext(Dispatchers.IO) {
        val today   = todayEpochDay()
        val current = contentCountRecordDao.getForDay(today)
        contentCountRecordDao.upsert(
            current?.copy(reelCount = current.reelCount + 1)
                ?: ContentCountRecord(epochDay = today, reelCount = 1)
        )
    }

    suspend fun incrementShortCount() = withContext(Dispatchers.IO) {
        val today   = todayEpochDay()
        val current = contentCountRecordDao.getForDay(today)
        contentCountRecordDao.upsert(
            current?.copy(shortCount = current.shortCount + 1)
                ?: ContentCountRecord(epochDay = today, shortCount = 1)
        )
    }

    suspend fun syncSessionsFromEvents(sinceMillis: Long) = withContext(Dispatchers.IO) {
        usageStatsHelper.getSessionEvents(sinceMillis).forEach { event ->
            when (event.eventType) {
                UsageEvents.Event.MOVE_TO_FOREGROUND -> openSession(event.packageName)
                UsageEvents.Event.MOVE_TO_BACKGROUND -> closeSession(event.packageName)
            }
        }
    }

    suspend fun pruneOldData() = withContext(Dispatchers.IO) {
        val cutoff = daysAgoEpochDay(30)
        usageRecordDao.deleteOlderThan(cutoff)
        sessionRecordDao.deleteOlderThan(cutoff)
        contentCountRecordDao.deleteOlderThan(cutoff)
        dailySummaryDao.deleteOlderThan(cutoff)
    }
}