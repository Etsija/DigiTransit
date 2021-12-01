package com.etsija.digitransit.model

import java.util.*

data class Alert(
    val description: String = "",
    val severity: String = "",
    val effectiveStartDate: String = "",
    val effectiveEndDate: String = "",
    val stopId: String? = "",
    val stopName: String? = "",
    val zoneId: String? = ""
) {

}