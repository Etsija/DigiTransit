package com.etsija.digitransit.model

data class Departure(
    val scheduledDeparture: Int = 0,
    val realtimeDeparture: Int = 0,
    val realtime: Boolean = false,
    val serviceDay: String? = "",
    val route: String? = "",
    val headsign: String? = null
)