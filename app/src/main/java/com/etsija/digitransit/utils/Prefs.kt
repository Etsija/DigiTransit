package com.etsija.digitransit.utils

import android.content.Context
import android.content.SharedPreferences

class Prefs (context: Context)
{
    private val preferences: SharedPreferences =
        context.getSharedPreferences("SHARED_PREFERENCES", Context.MODE_PRIVATE)

    var searchRadius: Int
        get() = preferences.getInt("SEARCH_RADIUS", 250)
        set(value) = preferences.edit().putInt("SEARCH_RADIUS", value).apply()

    var locInterval: Int
        get() = preferences.getInt("LOCATIONING_INTERVAL", 20)
        set(value) = preferences.edit().putInt("LOCATIONING_INTERVAL", value).apply()

    var stopsSearchInterval: Int
        get() = preferences.getInt("STOPS_SEARCH_INTERVAL", 20)
        set(value) = preferences.edit().putInt("STOPS_SEARCH_INTERVAL", value).apply()

    var arrivalsSearchInterval: Int
        get() = preferences.getInt("ARRIVALS_SEARCH_INTERVAL", 10)
        set(value) = preferences.edit().putInt("ARRIVALS_SEARCH_INTERVAL", value).apply()

    var lastLat: String?
        get() = preferences.getString("LAST_LAT", "60.2068726")
        set(value) = preferences.edit().putString("LAST_LAT", value).apply()

    var lastLon: String?
        get() = preferences.getString("LAST_LON", "60.2068726")
        set(value) = preferences.edit().putString("LAST_LON", value).apply()

}