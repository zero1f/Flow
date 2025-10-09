package com.zero.flow.util

object TimeFormatter {

    /**
     * Formats milliseconds to MM:SS format
     */
    fun formatTime(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    /**
     * Formats milliseconds to HH:MM:SS format
     */
    fun formatTimeLong(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    /**
     * Formats milliseconds to human-readable duration
     */
    fun formatDuration(milliseconds: Long): String {
        val totalMinutes = milliseconds / (1000 * 60)
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        return when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            minutes > 0 -> "${minutes}m"
            else -> "0m"
        }
    }

    /**
     * Formats seconds to MM:SS format
     */
    fun formatSeconds(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    /**
     * Parses MM:SS format to milliseconds
     */
    fun parseTimeToMillis(timeString: String): Long? {
        return try {
            val parts = timeString.split(":")
            if (parts.size != 2) return null

            val minutes = parts[0].toLongOrNull() ?: return null
            val seconds = parts[1].toLongOrNull() ?: return null

            if (seconds >= 60) return null

            (minutes * 60 + seconds) * 1000
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Gets relative time text (e.g., "2 hours ago", "just now")
     */
    fun getRelativeTimeText(dateTime: java.time.LocalDateTime): String {
        val now = java.time.LocalDateTime.now()
        val duration = java.time.Duration.between(dateTime, now)

        return when {
            duration.toMinutes() < 1 -> "just now"
            duration.toMinutes() < 60 -> "${duration.toMinutes()} minutes ago"
            duration.toHours() < 24 -> "${duration.toHours()} hours ago"
            duration.toDays() < 7 -> "${duration.toDays()} days ago"
            duration.toDays() < 30 -> "${duration.toDays() / 7} weeks ago"
            duration.toDays() < 365 -> "${duration.toDays() / 30} months ago"
            else -> "${duration.toDays() / 365} years ago"
        }
    }

    /**
     * Formats time remaining text
     */
    fun formatRemainingTime(milliseconds: Long): String {
        val totalMinutes = (milliseconds / (1000 * 60)).toInt()

        return when {
            totalMinutes < 1 -> "Less than a minute"
            totalMinutes == 1 -> "1 minute"
            totalMinutes < 60 -> "$totalMinutes minutes"
            else -> {
                val hours = totalMinutes / 60
                val minutes = totalMinutes % 60
                when {
                    minutes == 0 -> "$hours hour${if (hours > 1) "s" else ""}"
                    else -> "$hours hour${if (hours > 1) "s" else ""} $minutes minute${if (minutes > 1) "s" else ""}"
                }
            }
        }
    }
}