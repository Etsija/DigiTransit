package com.etsija.digitransit.model

import com.etsija.digitransit.StopsByRadiusQuery

data class Stop(
    val id: String,
    val gtfsId: String = "",
    val stopName: String = "",
    val stopCode: String = "",
    val distance: Int?,
    val patterns: List<Pattern?>?,
    val type: String?
)

