package com.etsija.digitransit.utils

import android.graphics.Color
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

        // Set the symbol (character) on a stop card based on stop type
        fun setCardSymbol(s: String): String {
            return when (s) {
                "TRAM" -> "R"
                "METRO" -> "M"
                "RAIL" -> "J"
                "BUS" -> "B"
                else -> ""
            }
        }

        // Choose the color of a stop card based on stop type
        fun setCardColor(s: String): Int {
            return when (s) {
                "TRAM" -> Color.parseColor("#008351")   // RAL 6024 Traffic Green, HKL Raitiovaunu
                "METRO" -> Color.parseColor("#F67828")  // RAL 2003 Pastel Orange, HKL Metro
                "RAIL" -> Color.parseColor("#844C82")   // RAL 4008 Signal Violet, HSL Lähijuna
                "BUS" -> Color.parseColor("#2271B3")    // RAL 5015 Sky Blue, HSL Bussi
                else -> Color.DKGRAY
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

        // Filter out the stop codes inside (...) from the pattern name
        fun tidyPatternName(s: String): String {
            return s.replace("\\s*\\([^\\)]*\\)\\s*".toRegex(), " ")
        }


    }
}