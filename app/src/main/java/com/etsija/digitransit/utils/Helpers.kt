package com.etsija.digitransit.utils

import android.content.Context
import android.graphics.Color
import android.location.Geocoder
import android.util.Log
import android.widget.ImageView
import com.etsija.digitransit.R
import com.etsija.digitransit.model.Pattern
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

class Helpers {

    companion object {

        // Get date from Unix Epoch in seconds (10 digits)
        fun getDateTime(s: String): String {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val netDate = sdf.format(s.toLong() * 1000L)
                return netDate
            } catch (e: Exception) {
                return e.toString()
            }
        }

        // Get hrs, min, sec from seconds -> returns string
        fun getHMS(totalSeconds: Int): String {
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60

            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }

        // Set the symbol (character) on a stop card based on stop type
        fun setCardSymbol(s: String): String {
            return when (s) {
                "TRAM" -> "R"
                "METRO" -> "M"
                "RAIL" -> "J"
                "BUS" -> "B"
                else -> "" // Stops outside HSL area and not identified with type
            }
        }

        fun setCardIcon(v: ImageView, s: String) {
            when (s) {
                "TRAM" -> v.setBackgroundResource(R.drawable.ic_baseline_tram_24)
                "RAIL" -> v.setBackgroundResource(R.drawable.ic_baseline_train_24)
                "BUS" -> v.setBackgroundResource(R.drawable.ic_baseline_directions_bus_24)
            }
        }

        // Choose the color of a stop card based on stop type
        fun setCardColor(s: String): Int {
            return when (s) {
                "TRAM" -> Color.parseColor("#008351")   // RAL 6024 Traffic Green, HKL Raitiovaunu
                "METRO" -> Color.parseColor("#F67828")  // RAL 2003 Pastel Orange, HKL Metro
                "RAIL" -> Color.parseColor("#844C82")   // RAL 4008 Signal Violet, HSL Lähijuna
                "BUS" -> Color.parseColor("#2271B3")    // RAL 5015 Sky Blue, HSL Bussi
                else -> Color.GRAY  // Stops outside HSL area and not identified with type
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

        // Get address based on location
        fun getAddress(context: Context, lat: Double, lon: Double): String? {
            val geoCoder = Geocoder(context, Locale.getDefault())
            val addresses = geoCoder.getFromLocation(lat, lon, 1)

            return addresses[0]
                .getAddressLine(0)
                .toString()
                .substringBefore(", ")
        }


    }
}