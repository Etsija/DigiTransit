package com.etsija.digitransit.mapper

import com.etsija.digitransit.StopArrDepQuery
import com.etsija.digitransit.model.Departure
import com.etsija.digitransit.utils.Helpers.Companion.getDateTime
import com.etsija.digitransit.utils.Helpers.Companion.getHMS

object DepartureMapper {

    fun buildFrom(response: StopArrDepQuery.StoptimesWithoutPattern): Departure {

        val timeNow = System.currentTimeMillis()/1000
        val timeDepScheduled = response.serviceDay.toString().toInt() + response.scheduledDeparture!!
        val timeDepRealtime = response.serviceDay.toString().toInt() + response.realtimeDeparture!!

        val secondsUntilScheduled = (timeDepScheduled - timeNow).toInt()
        val secondsUntilRealtime = (timeDepRealtime - timeNow).toInt()

        val strUntilScheduled = getHMS(secondsUntilScheduled)
        val strUntilRealtime = getHMS(secondsUntilRealtime)

        return Departure(
            strUntilScheduled,
            strUntilRealtime,
            response.realtime!!,
            getDateTime(response.serviceDay.toString()),
            response.trip?.route?.shortName,
            response.headsign
        )
    }
}