package com.etsija.digitransit.model

data class Stop(
    val id: String,
    val gtfsId: String,
    val stopName: String,
    val stopCode: String?,
    val zoneId: String?,
    val parentName: String?,
    val distance: Int?,
    val patterns: List<Pattern?>?,
    val type: String?
)

