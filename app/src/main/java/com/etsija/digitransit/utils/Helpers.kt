package com.etsija.digitransit.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.location.Geocoder
import android.util.Log
import android.widget.ImageView
import androidx.core.app.NotificationCompat.getColor
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.etsija.digitransit.R
import com.etsija.digitransit.model.Pattern
import com.etsija.digitransit.model.Stop
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import java.lang.Integer.toHexString
import java.security.AccessController.getContext
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
        fun setCardColor(type: String): Int {
            return when (type) {
                "TRAM" -> ResourcesCompat.getColor(App.res, R.color.HKL_raitiovaunu, null)
                "METRO" -> ResourcesCompat.getColor(App.res, R.color.HKL_metro, null)
                "RAIL" -> ResourcesCompat.getColor(App.res, R.color.HKL_lahijuna, null)
                "BUS" -> ResourcesCompat.getColor(App.res, R.color.HKL_bussi, null)
                else -> Color.GRAY  // Stops with unknown type
            }
        }

        // Set the pattern color depending on inbound/outbound pattern
        fun setPatternColor(s: String): Int {
            return when (s) {
                "OUTBOUND" -> ResourcesCompat.getColor(App.res, android.R.color.holo_green_light, null)
                "INBOUND" -> ResourcesCompat.getColor(App.res, android.R.color.holo_red_light, null)
                else -> Color.WHITE
            }
        }

        // Convert color value from Int to String
        fun colorToHex(c: Int): String {
            return "#" + toHexString(c and 0xffffff)
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

        // This method takes in the pattern number list
        // and creates a colored list based on whether the pattern is INBOUND, OUTBOUND
        // or UNDEFINED. The method returns the colored list as an HTML string.
        //
        // Usage in TextView: tvMyTextView.text = Html.fromHtml(text, FROM_HTML_MODE_LEGACY
        fun getPatternNumbersColored(patterns: List<Pattern?>?): String? {
            val pairList = arrayListOf<Pair<String, String?>>()
            val outList: List<Pair<String, String?>>
            var retString: String? = ""

            // Construct a list of pairs (pattern number, direction)
            patterns?.map { pattern ->
                pairList.add(Pair(getPatternNumber(pattern!!.name), pattern.direction))
            }
            outList = pairList.sortedWith(compareBy { it.first }).distinct()

            // Create the HTML and colour each element according to whether the pattern
            // is set to INBOUND or OUTBOUND route
            for (l in outList) {
                retString +=
                    "<font color=" +
                    l.second?.let { colorToHex(setPatternColor(it)) } +
                    ">" +
                    l.first +
                    " " +
                    "</font>"
            }
            return (retString)
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