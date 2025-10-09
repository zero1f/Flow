package com.zero.flow.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.content.getSystemService
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Extensions for LocalDateTime
 */
fun LocalDateTime.toDisplayDate(): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    return this.format(formatter)
}

fun LocalDateTime.toDisplayTime(): String {
    val formatter = DateTimeFormatter.ofPattern("hh:mm a")
    return this.format(formatter)
}

fun LocalDateTime.toDisplayDateTime(): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a")
    return this.format(formatter)
}

fun LocalDateTime.startOfDay(): LocalDateTime {
    return this.truncatedTo(ChronoUnit.DAYS)
}

fun LocalDateTime.endOfDay(): LocalDateTime {
    return this.startOfDay().plusDays(1).minusNanos(1)
}

fun LocalDateTime.startOfWeek(): LocalDateTime {
    return this.minusDays(this.dayOfWeek.value.toLong() - 1).startOfDay()
}

fun LocalDateTime.endOfWeek(): LocalDateTime {
    return this.startOfWeek().plusDays(7).minusNanos(1)
}

fun LocalDateTime.startOfMonth(): LocalDateTime {
    return this.withDayOfMonth(1).startOfDay()
}

fun LocalDateTime.endOfMonth(): LocalDateTime {
    return this.plusMonths(1).withDayOfMonth(1).startOfDay().minusNanos(1)
}

fun LocalDateTime.isSameDay(other: LocalDateTime): Boolean {
    return this.toLocalDate() == other.toLocalDate()
}

fun LocalDateTime.daysUntil(other: LocalDateTime): Long {
    return ChronoUnit.DAYS.between(this.toLocalDate(), other.toLocalDate())
}

/**
 * Extensions for Long (time in milliseconds)
 */
fun Long.toMinutes(): Long {
    return this / (1000 * 60)
}

fun Long.toHoursAndMinutes(): Pair<Long, Long> {
    val totalMinutes = this.toMinutes()
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return Pair(hours, minutes)
}

fun Long.formatDuration(): String {
    val (hours, minutes) = this.toHoursAndMinutes()
    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        else -> "${minutes}m"
    }
}

/**
 * Extensions for Int (minutes)
 */
fun Int.minutesToMillis(): Long {
    return this * 60L * 1000L
}

/**
 * Context Extensions
 */
fun Context.vibrate(pattern: LongArray = Constants.VIBRATION_PATTERN_SHORT) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = getSystemService<VibratorManager>()
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService<Vibrator>()
    }

    vibrator?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            it.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            it.vibrate(pattern, -1)
        }
    }
}

/**
 * Collection Extensions
 */
fun <T> List<T>.sumOfLong(selector: (T) -> Long): Long {
    return this.fold(0L) { acc, item -> acc + selector(item) }
}