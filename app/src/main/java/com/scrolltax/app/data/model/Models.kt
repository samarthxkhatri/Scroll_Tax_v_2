package com.scrolltax.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

const val PACKAGE_INSTAGRAM = "com.instagram.android"
const val PACKAGE_YOUTUBE   = "com.google.android.youtube"

@Entity(tableName = "usage_records")
data class UsageRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val epochDay: Long,
    val packageName: String,
    val totalTimeMs: Long,
    val lastUpdatedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "session_records")
data class SessionRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val startTime: Long,
    val endTime: Long?,
    val durationMs: Long = if (endTime != null) endTime - startTime else 0L,
    val epochDay: Long,
)

@Entity(tableName = "content_count_records")
data class ContentCountRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val epochDay: Long,
    val reelCount: Int = 0,
    val shortCount: Int = 0,
    val totalScrollCount: Int = reelCount + shortCount,
)

@Entity(tableName = "daily_summaries")
data class DailySummary(
    @PrimaryKey
    val epochDay: Long,
    val instagramTimeMs: Long = 0L,
    val youtubeTimeMs: Long   = 0L,
    val reelCount: Int        = 0,
    val shortCount: Int       = 0,
    val sessionCount: Int     = 0,
    val totalScrolls: Int     = reelCount + shortCount,
    val totalTimeMs: Long     = instagramTimeMs + youtubeTimeMs,
    val lastUpdatedAt: Long   = System.currentTimeMillis(),
)

data class ScrollDebt(
    val totalReels: Int,
    val equivalents: List<DebtEquivalent>,
)

data class DebtEquivalent(
    val emoji: String,
    val label: String,
    val quantity: String,
)