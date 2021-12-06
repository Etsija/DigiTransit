package com.etsija.digitransit.model

import com.etsija.digitransit.StopsByRadiusQuery

data class Stop(
    val gtfsId: String = "",
    val stopName: String = "",
    val stopCode: String = "",
    val distance: Int?,
    val type: String?
)

