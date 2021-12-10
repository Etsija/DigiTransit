package com.etsija.digitransit.mapper

import com.etsija.digitransit.AlertsQuery
import com.etsija.digitransit.model.Alert
import com.etsija.digitransit.utils.Helpers.Companion.getDateTime

object AlertMapper {

    // This should now return a null-safe domain model
    fun buildFrom(response: AlertsQuery.Alert): Alert {
        return Alert(
            response.id,
            response.feed,
            response.alertDescriptionText,
            response.alertSeverityLevel?.toString(),
            response.effectiveStartDate?.toString()?.toDouble(),
            response.effectiveStartDate?.toString()?.let { getDateTime(it) },
            response.effectiveEndDate?.toString()?.let { getDateTime(it) },
            response.stop?.gtfsId,
            response.stop?.code,
            response.stop?.name,
            response.stop?.zoneId
        )
    }
}