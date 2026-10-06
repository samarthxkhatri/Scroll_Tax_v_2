package com.scrolltax.app.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.scrolltax.app.data.model.PACKAGE_INSTAGRAM
import com.scrolltax.app.data.model.PACKAGE_YOUTUBE
import com.scrolltax.app.data.repository.UsageRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class ScrollTaxAccessibilityService : AccessibilityService() {

    companion object {
        private const val DEBOUNCE_MS = 400L
        const val ACTION_COUNT_UPDATE = "com.scrolltax.app.COUNT_UPDATE"
        const val EXTRA_PACKAGE       = "extra_package"
        const val EXTRA_COUNT_TYPE    = "extra_count_type"
    }

    @Inject lateinit var usageRepository: UsageRepository
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var lastReelEventMs  = 0L
    private var lastShortEventMs = 0L

    override fun onServiceConnected() {
        serviceInfo = AccessibilityServiceInfo().apply {
            eventTypes   = AccessibilityEvent.TYPE_VIEW_SCROLLED or AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            packageNames = arrayOf(PACKAGE_INSTAGRAM, PACKAGE_YOUTUBE)
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            notificationTimeout = DEBOUNCE_MS
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        try {
            when (event.packageName?.toString()) {
                PACKAGE_INSTAGRAM -> handleInstagramEvent(event)
                PACKAGE_YOUTUBE   -> handleYouTubeEvent(event)
            }
        } catch (e: Exception) { /* never crash on a11y events */ }
    }

    override fun onInterrupt() {}
    override fun onDestroy() { super.onDestroy(); serviceScope.cancel() }

    private fun handleInstagramEvent(event: AccessibilityEvent) {
        if (event.eventType != AccessibilityEvent.TYPE_VIEW_SCROLLED) return
        val source = event.source ?: return
        val now = System.currentTimeMillis()
        if (now - lastReelEventMs < DEBOUNCE_MS) return
        lastReelEventMs = now
        source.recycle()
        serviceScope.launch { usageRepository.incrementReelCount() }
        sendBroadcast(Intent(ACTION_COUNT_UPDATE).putExtra(EXTRA_PACKAGE, PACKAGE_INSTAGRAM).putExtra(EXTRA_COUNT_TYPE, "reel"))
    }

    private fun handleYouTubeEvent(event: AccessibilityEvent) {
        if (event.eventType != AccessibilityEvent.TYPE_VIEW_SCROLLED &&
            event.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) return
        val source = event.source ?: return
        if (!isYouTubeShortsContext(source)) return
        val now = System.currentTimeMillis()
        if (now - lastShortEventMs < DEBOUNCE_MS) return
        lastShortEventMs = now
        source.recycle()
        serviceScope.launch { usageRepository.incrementShortCount() }
        sendBroadcast(Intent(ACTION_COUNT_UPDATE).putExtra(EXTRA_PACKAGE, PACKAGE_YOUTUBE).putExtra(EXTRA_COUNT_TYPE, "short"))
    }

    private fun isYouTubeShortsContext(node: AccessibilityNodeInfo): Boolean {
        var current: AccessibilityNodeInfo? = node
        var depth = 0
        while (current != null && depth < 8) {
            val viewId = current.viewIdResourceName?.lowercase() ?: ""
            if (viewId.contains("shorts") || viewId.contains("reel")) return true
            val parent = current.parent
            if (current != node) current.recycle()
            current = parent
            depth++
        }
        return false
    }
}