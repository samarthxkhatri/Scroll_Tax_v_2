package com.scrolltax.app.data.dao

import androidx.room.*
import com.scrolltax.app.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UsageRecordDao {
    @Query("SELECT * FROM usage_records WHERE epochDay = :day AND packageName = :pkg LIMIT 1")
    suspend fun getForDayAndPackage(day: Long, pkg: String): UsageRecord?

    @Query("SELECT * FROM usage_records WHERE epochDay = :day")
    fun observeForDay(day: Long): Flow<List<UsageRecord>>

    @Query("SELECT * FROM usage_records WHERE epochDay >= :fromDay ORDER BY epochDay ASC")
    fun observeSince(fromDay: Long): Flow<List<UsageRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: UsageRecord)

    @Query("DELETE FROM usage_records WHERE epochDay < :cutoffDay")
    suspend fun deleteOlderThan(cutoffDay: Long)
}

@Dao
interface SessionRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: SessionRecord): Long

    @Query("UPDATE session_records SET endTime = :endTime, durationMs = :durationMs WHERE id = :id")
    suspend fun closeSession(id: Long, endTime: Long, durationMs: Long)

    @Query("SELECT * FROM session_records WHERE endTime IS NULL AND packageName = :pkg LIMIT 1")
    suspend fun getOpenSession(pkg: String): SessionRecord?

    @Query("SELECT * FROM session_records WHERE epochDay = :day ORDER BY startTime DESC")
    fun observeSessionsForDay(day: Long): Flow<List<SessionRecord>>

    @Query("SELECT COUNT(*) FROM session_records WHERE epochDay = :day")
    fun observeSessionCountForDay(day: Long): Flow<Int>

    @Query("DELETE FROM session_records WHERE epochDay < :cutoffDay")
    suspend fun deleteOlderThan(cutoffDay: Long)
}

@Dao
interface ContentCountRecordDao {
    @Query("SELECT * FROM content_count_records WHERE epochDay = :day LIMIT 1")
    suspend fun getForDay(day: Long): ContentCountRecord?

    @Query("SELECT * FROM content_count_records WHERE epochDay = :day LIMIT 1")
    fun observeForDay(day: Long): Flow<ContentCountRecord?>

    @Query("SELECT * FROM content_count_records WHERE epochDay >= :fromDay ORDER BY epochDay ASC")
    fun observeSince(fromDay: Long): Flow<List<ContentCountRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: ContentCountRecord)

    @Query("DELETE FROM content_count_records WHERE epochDay < :cutoffDay")
    suspend fun deleteOlderThan(cutoffDay: Long)
}

@Dao
interface DailySummaryDao {
    @Query("SELECT * FROM daily_summaries WHERE epochDay = :day LIMIT 1")
    fun observeForDay(day: Long): Flow<DailySummary?>

    @Query("SELECT * FROM daily_summaries WHERE epochDay >= :fromDay ORDER BY epochDay ASC")
    fun observeSince(fromDay: Long): Flow<List<DailySummary>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(summary: DailySummary)

    @Query("DELETE FROM daily_summaries WHERE epochDay < :cutoffDay")
    suspend fun deleteOlderThan(cutoffDay: Long)
}