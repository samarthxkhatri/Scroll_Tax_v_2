package com.scrolltax.app.util

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Process
import com.scrolltax.app.data.model.PACKAGE_INSTAGRAM
import com.scrolltax.app.data.model.PACKAGE_YOUTUBE
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsageStatsHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val usageStatsManager: UsageStatsManager by lazy {
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    }

    fun hasUsageStatsPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName,
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun epochDayToRange(epochDay: Long): Pair<Long, Long> {
        val zone  = ZoneId.systemDefault()
        val date  = LocalDate.ofEpochDay(epochDay)
        val start = date.atStartOfDay(zone).toInstant().toEpochMilli()
        val end   = date.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        return start to end
    }

    fun getTotalTimeForDay(packageName: String, epochDay: Long): Long {
        if (!hasUsageStatsPermission()) return 0L
        val (start, end) = epochDayToRange(epochDay)
        return try {
            val statsMap = usageStatsManager.queryAndAggregateUsageStats(start, end)
            statsMap[packageName]?.totalTimeInForeground ?: 0L
        } catch (e: Exception) { 0L }
    }

    fun getInstagramTimeToday(): Long =
        getTotalTimeForDay(PACKAGE_INSTAGRAM, LocalDate.now().toEpochDay())

    fun getYouTubeTimeToday(): Long =
        getTotalTimeForDay(PACKAGE_YOUTUBE, LocalDate.now().toEpochDay())

    data class AppEvent(val packageName: String, val eventType: Int, val timestamp: Long)

    fun getSessionEvents(startTime: Long, endTime: Long = System.currentTimeMillis()): List<AppEvent> {
        if (!hasUsageStatsPermission()) return emptyList()
        val tracked = setOf(PACKAGE_INSTAGRAM, PACKAGE_YOUTUBE)
        val events  = mutableListOf<AppEvent>()
        return try {
            val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
            val event = UsageEvents.Event()
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event)
                if (event.packageName in tracked &&
                    (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND ||
                            event.eventType == UsageEvents.Event.MOVE_TO_BACKGROUND)) {
                    events += AppEvent(event.packageName, event.eventType, event.timeStamp)
                }
            }
            events
        } catch (e: Exception) { emptyList() }
    }

    fun getForegroundApp(): String? {
        if (!hasUsageStatsPermission()) return null
        val now = System.currentTimeMillis()
        return usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, now - 60_000L, now,
        ).maxByOrNull { it.lastTimeUsed }?.packageName
    }
}