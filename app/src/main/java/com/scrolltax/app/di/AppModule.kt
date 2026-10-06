package com.scrolltax.app.di

import android.content.Context
import androidx.room.Room
import com.scrolltax.app.data.dao.*
import com.scrolltax.app.data.database.ScrollTaxDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ScrollTaxDatabase =
        Room.databaseBuilder(
            context,
            ScrollTaxDatabase::class.java,
            "scrolltax_database",
        ).fallbackToDestructiveMigration().build()

    @Provides fun provideUsageRecordDao(db: ScrollTaxDatabase): UsageRecordDao = db.usageRecordDao()
    @Provides fun provideSessionRecordDao(db: ScrollTaxDatabase): SessionRecordDao = db.sessionRecordDao()
    @Provides fun provideContentCountRecordDao(db: ScrollTaxDatabase): ContentCountRecordDao = db.contentCountRecordDao()
    @Provides fun provideDailySummaryDao(db: ScrollTaxDatabase): DailySummaryDao = db.dailySummaryDao()

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context
}