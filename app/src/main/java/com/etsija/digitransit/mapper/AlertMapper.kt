package com.etsija.digitransit.mapper

import com.etsija.digitransit.AlertsQuery
import com.etsija.digitransit.model.Alert
import com.etsija.digitransit.utils.Helpers.Companion.getDateTime

object AlertMapper {

    fun buildFrom(response: AlertsQuery.Alert): Alert {
        return Alert(
            response.alertDescriptionText,
            response.alertSeverityLevel.toString(),
            getDateTime(response.effectiveStartDate.toString())
        )
    }
}