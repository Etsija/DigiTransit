package com.etsija.digitransit.mapper

import com.etsija.digitransit.StopArrDepQuery
import com.etsija.digitransit.model.Departure

object DepartureMapper {

    fun buildFrom(response: StopArrDepQuery.StoptimesWithoutPattern): Departure {
        return Departure(
            response.scheduledDeparture!!,
            response.realtimeDeparture!!,
            response.realtime!!,
            response.serviceDay.toString(),
            response.trip?.route?.shortName!!,
            response.headsign
        )
    }
}