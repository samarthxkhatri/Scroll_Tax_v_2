package com.scrolltax.app.util

import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.TimeUnit

fun Long.toHoursMinutesString(): String {
    val hours   = TimeUnit.MILLISECONDS.toHours(this)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this) % 60
    return when {
        hours > 0   -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m"
        else        -> "<1m"
    }
}

fun Long.toHhMmSsString(): String {
    val h = TimeUnit.MILLISECONDS.toHours(this)
    val m = TimeUnit.MILLISECONDS.toMinutes(this) % 60
    val s = TimeUnit.MILLISECONDS.toSeconds(this) % 60
    return "%02d:%02d:%02d".format(h, m, s)
}

fun todayEpochDay(): Long = LocalDate.now().toEpochDay()

fun daysAgoEpochDay(days: Long): Long = LocalDate.now().minusDays(days).toEpochDay()

fun Long.toReadableDate(): String {
    val date = LocalDate.ofEpochDay(this)
    return "${date.dayOfWeek.name.take(3)}, ${date.dayOfMonth} ${date.month.name.take(3)}"
}

fun Long.toMinutesFloat(): Float = this / 60_000f

fun LocalDate.startOfDayMillis(): Long =
    atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()