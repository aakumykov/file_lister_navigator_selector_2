package com.github.aakumykov.file_lister_navigator_selector.utils

import java.text.SimpleDateFormat
import java.util.Locale

class DateFormatter {
    companion object {
        fun humanReadableDate(timestamp: Long): String {
            return SimpleDateFormat("HH:MM:SS DD:mm:yyyy", Locale.getDefault()).format(timestamp)
        }
    }
}