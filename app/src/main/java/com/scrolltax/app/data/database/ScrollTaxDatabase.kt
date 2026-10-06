package com.scrolltax.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.scrolltax.app.data.dao.*
import com.scrolltax.app.data.model.*

@Database(
    entities = [
        UsageRecord::class,
        SessionRecord::class,
        ContentCountRecord::class,
        DailySummary::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class ScrollTaxDatabase : RoomDatabase() {
    abstract fun usageRecordDao(): UsageRecordDao
    abstract fun sessionRecordDao(): SessionRecordDao
    abstract fun contentCountRecordDao(): ContentCountRecordDao
    abstract fun dailySummaryDao(): DailySummaryDao
}