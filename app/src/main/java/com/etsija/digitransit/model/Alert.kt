package com.etsija.digitransit.model

import java.util.*

data class Alert(
    val id: String,
    val feed: String?,
    val description: String,
    val severity: String?,
    val effectiveStartDateAsDouble: Double?,
    val effectiveStartDate: String?,
    val effectiveEndDate: String?,
    val stopId: String?,
    val stopCode: String?,
    val stopName: String?,
    val zoneId: String?
) {

}