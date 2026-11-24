package com.microalert.app.utils

import com.microalert.app.presentation.theme.Lang
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {

    private val timeOnlyFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    fun formatSessionDate(timestampMs: Long): String {
        val loc = if (Lang.isEnglish) Locale.ENGLISH else Locale("es")
        return SimpleDateFormat("dd MMM, hh:mm a", loc).format(Date(timestampMs))
    }

    fun formatTimeOnly(timestampMs: Long): String {
        return timeOnlyFormat.format(Date(timestampMs))
    }

    fun formatDuration(durationSeconds: Long): String {
        val hours = durationSeconds / 3600
        val minutes = (durationSeconds % 3600) / 60
        val seconds = durationSeconds % 60

        return when {
            hours > 0 -> String.format(Locale.getDefault(), "%dh %02dm", hours, minutes)
            minutes > 0 -> String.format(Locale.getDefault(), "%d min %02ds", minutes, seconds)
            else -> String.format(Locale.getDefault(), "%ds", seconds)
        }
    }

    fun formatClock(totalSeconds: Long): String {
        val h = totalSeconds / 3600
        val m = (totalSeconds % 3600) / 60
        val s = totalSeconds % 60
        return if (h > 0) String.format(Locale.US, "%d:%02d:%02d", h, m, s)
        else String.format(Locale.US, "%02d:%02d", m, s)
    }

    fun formatHeartRate(bpm: Float?): String {
        return if (bpm != null && bpm > 0) "${bpm.toInt()} BPM" else "N/D"
    }
}
