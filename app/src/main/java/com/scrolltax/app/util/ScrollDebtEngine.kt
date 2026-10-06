package com.scrolltax.app.util

import com.scrolltax.app.data.model.DebtEquivalent
import com.scrolltax.app.data.model.ScrollDebt
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class ScrollDebtEngine @Inject constructor() {

    fun compute(totalScrolls: Int, avgReelDurationSeconds: Int = 30): ScrollDebt {
        val totalMinutes = (totalScrolls * avgReelDurationSeconds) / 60.0
        val equivalents = buildList {
            val dsaSessions = (totalMinutes / 45.0).roundToInt()
            if (dsaSessions > 0) add(DebtEquivalent("🧠", "DSA practice sessions", "$dsaSessions"))
            val pages = (totalMinutes / 2.0).roundToInt()
            if (pages > 0) add(DebtEquivalent("📖", "pages of reading", "$pages"))
            val workouts = (totalMinutes / 45.0).roundToInt()
            if (workouts > 0) add(DebtEquivalent("🏋️", "full workouts", "$workouts"))
            val milestones = (totalMinutes / 60.0).roundToInt()
            if (milestones > 0) add(DebtEquivalent("💻", "coding milestones", "$milestones"))
        }
        return ScrollDebt(totalReels = totalScrolls, equivalents = equivalents.take(4))
    }

    fun getAwarenessMessage(totalScrolls: Int, avgReelDurationSeconds: Int = 30): String {
        val totalMinutes = (totalScrolls * avgReelDurationSeconds) / 60
        return when {
            totalScrolls == 0   -> "No scrolling detected yet. Great start."
            totalScrolls < 20   -> "You've watched $totalScrolls reels. Stay conscious."
            totalScrolls < 60   -> "You've watched $totalScrolls reels — about ${totalMinutes}m of your attention."
            totalScrolls < 120  -> "You've watched $totalScrolls reels. That's ${totalMinutes / 60}h+ of your day."
            else                -> "🚨 $totalScrolls reels. That is ${totalMinutes / 60}h of deep-work time."
        }
    }
}