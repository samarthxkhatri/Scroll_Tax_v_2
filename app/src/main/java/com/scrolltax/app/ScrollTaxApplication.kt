package com.scrolltax.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.scrolltax.app.notification.NotificationChannelManager
import com.scrolltax.app.worker.WorkerScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ScrollTaxApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var notificationChannelManager: NotificationChannelManager
    @Inject lateinit var workerScheduler: WorkerScheduler

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()
        notificationChannelManager.createChannels()
        workerScheduler.schedulePeriodicSync()
    }
}