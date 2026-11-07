package com.microalert.app.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {

    private val sessionDateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    private val timeOnlyFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    fun formatSessionDate(timestampMs: Long): String {
        return sessionDateFormat.format(Date(timestampMs))
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

    fun formatHeartRate(bpm: Float?): String {
        return if (bpm != null && bpm > 0) "${bpm.toInt()} BPM" else "N/D"
    }
}
