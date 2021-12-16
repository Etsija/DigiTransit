package com.etsija.digitransit.utils

import android.content.Context
import android.graphics.Color
import android.location.Geocoder
import android.util.Log
import android.widget.ImageView
import com.etsija.digitransit.R
import com.etsija.digitransit.model.Pattern
import com.etsija.digitransit.model.Stop
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

        // Choose the color of a stop card based on stop type
        fun setPatternColor(s: String): Int {
            return when (s) {
                "OUTBOUND" -> android.R.color.holo_green_dark
                "INBOUND" -> android.R.color.holo_red_dark
                else -> Color.GRAY
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

        //
        // This method takes in a Stop and finds all possible next stops of it
        //
        // It does so by running through lists stop(Stop)->patterns(Pattern)->stops(PatternStop),
        // matching the current stop in the PatternStop lists and choosing the next stop in each
        // list (if available).
        // Because most stops have only one possible next stop, those results are in the end
        // combined, and the method returns a string containing only the distinct next stops
        // of this stop.
        fun findNextStops(stop: Stop): String {
            val nextStops: ArrayList<String?> = arrayListOf()

            // If there are no patterns through this stop
            stop.patterns ?: return ""

            val thisStopId = stop.gtfsId

            // Find the next stop name for all patterns through this stop
            stop.patterns.forEach { pattern ->
                val itr = pattern?.patternStops?.listIterator()
                if (itr != null) {
                    while (itr.hasNext()) {
                        if (itr.next()?.gtfsId == thisStopId) {
                            if (itr.hasNext()) {
                                nextStops.add(itr.next()?.name)
                            } else {
                                nextStops.add("Päätepysäkki")
                            }
                        }
                    }
                }
            }

            // Sort the list and return only unique list of next stops
            val strNextStops = nextStops
                .map { it }
                .sortedBy { it }
                .distinct()
                .joinToString(separator = ", ")

            // In case there is no stop after this one, the stop is a terminus, which is returned
            return when (strNextStops) {
                "Päätepysäkki" -> strNextStops
                else -> "-> $strNextStops"
            }
        }

        // Filter out the stop codes inside (...) from the pattern name
        fun tidyPatternName(s: String): String {
            return s.replace("\\s*\\([^\\)]*\\)\\s*".toRegex(), " ")
        }

        // Get address based on location
        fun getAddress(context: Context, lat: Double, lon: Double): String? {
            val geoCoder = Geocoder(context, Locale.getDefault())
            val addresses = geoCoder.getFromLocation(lat, lon, 1)
            var address: String? = ""

            try {
                address = addresses[0]
                    .getAddressLine(0)
                    .toString()
                    .substringBefore(", ")
            } catch (e: Exception) {
                Log.d("getAddress()", e.toString())
            }
            return address
        }

        fun testLocations(case: Int): Pair<String, String> {
            var lat: String = "60.2068726"
            var lon: String = "24.8939462"

            when (case) {

                // Helsinki / Rautatieasema
                1 -> {
                    lat = "60.171323"
                    lon = "24.940923"
                }

                // Helsinki / Huopalahden asema
                2 -> {
                    lat = "60.218564"
                    lon = "24.892657"
                }

                // Helsinki / Aarnin talo
                3 -> {
                    lat = "60.1872239"
                    lon = "24.9533152"
                }

                // Vantaa / Tikkurilan asema
                4 -> {
                    lat = "60.293350"
                    lon = "25.044936"
                }

                // Hyvinkää / Minnan talo
                5 -> {
                    lat = "60.608580"
                    lon = "24.838765"
                }

                // Mikkeli / Harri Häkkisen talo
                6 -> {
                    lat = "61.6888813"
                    lon = "27.2577625"
                }

                // Oulu / Tuiran sillat
                7 -> {
                    lat = "65.0156201"
                    lon = "25.4697043"
                }
            }

            return Pair(lat, lon)
        }
    }
}