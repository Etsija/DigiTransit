package com.etsija.digitransit.utils

import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

class Helpers {

    companion object {
        fun getDateTime(s: String): String {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val netDate = sdf.format(s.toLong() * 1000L)
                return netDate
            } catch (e: Exception) {
                return e.toString()
            }
        }
    }
}