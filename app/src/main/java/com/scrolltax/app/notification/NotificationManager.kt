package com.scrolltax.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.scrolltax.app.R
import com.scrolltax.app.ui.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

const val CHANNEL_AWARENESS = "scrolltax_awareness"
const val CHANNEL_SESSION   = "scrolltax_session"
const val CHANNEL_REPORT    = "scrolltax_report"

@Singleton
class NotificationChannelManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun createChannels() {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(NotificationChannel(CHANNEL_AWARENESS, "Awareness Reminders", NotificationManager.IMPORTANCE_DEFAULT))
        nm.createNotificationChannel(NotificationChannel(CHANNEL_SESSION,   "Session Alerts",      NotificationManager.IMPORTANCE_HIGH))
        nm.createNotificationChannel(NotificationChannel(CHANNEL_REPORT,    "Daily Reports",       NotificationManager.IMPORTANCE_LOW))
    }
}

@Singleton
class NotificationSender @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val nm = NotificationManagerCompat.from(context)

    private fun buildTapIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    fun sendThirtyMinuteAlert(reelCount: Int) = post(1001, CHANNEL_AWARENESS,
        "30 minutes of scrolling",
        if (reelCount > 0) "You have watched approximately $reelCount reels today."
        else "Instagram tracks your engagement. ScrollTax tracks the cost.")

    fun sendOneHourAlert(reelCount: Int, totalMinutes: Int) = post(1002, CHANNEL_SESSION,
        "1 hour of short-form content",
        "You have spent $totalMinutes minutes scrolling today. That's $reelCount pieces of content.")

    fun sendNinetyMinuteAlert(reelCount: Int) = post(1003, CHANNEL_SESSION,
        "⚠️ 90-minute scroll session",
        "\"Every reel costs more than you think.\" You've watched ~$reelCount reels.")

    fun sendLimitExceededAlert(limitMinutes: Int, actualMinutes: Int) = post(1004, CHANNEL_SESSION,
        "Daily limit exceeded",
        "You set a ${limitMinutes}min goal. You've reached ${actualMinutes}min.")

    private fun post(id: Int, channel: String, title: String, body: String) {
        try {
            nm.notify(id, NotificationCompat.Builder(context, channel)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setContentIntent(buildTapIntent())
                .setAutoCancel(true)
                .build())
        } catch (e: SecurityException) { /* permission not granted */ }
    }
}