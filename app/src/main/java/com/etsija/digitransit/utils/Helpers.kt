package com.etsija.digitransit.utils

import com.etsija.digitransit.model.Pattern
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

        // Return only the list of pattern numbers (via a stop). Note that sorting would
        // need regexp handling, as the order is not correct (321 comes before 37)
        fun getPatternNumbers(patterns: List<Pattern?>?): String? {
            val retString = patterns
                ?.map { pattern ->
                    getPatternNumber(pattern!!.name)
                }?.sorted()?.distinct()?.joinToString(separator = ", ")
            return retString
        }

        // Extract only the pattern number from a longer name of the pattern
        fun getPatternNumber(s: String): String {
            return s.substringBefore(" ")
        }

        // Filter out the stop codes from the pattern name
        fun tidyPatternName(s: String): String {
            return s.replace("\\s*\\([^\\)]*\\)\\s*".toRegex(), " ")
        }
    }
}