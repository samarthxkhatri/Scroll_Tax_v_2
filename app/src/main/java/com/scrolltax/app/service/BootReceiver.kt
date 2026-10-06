package com.scrolltax.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.scrolltax.app.worker.WorkerScheduler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject lateinit var workerScheduler: WorkerScheduler
    override fun onReceive(context: Context, intent: Intent) {
        workerScheduler.schedulePeriodicSync()
    }
}