package com.etsija.digitransit.mapper

import com.etsija.digitransit.StopsByRadiusQuery
import com.etsija.digitransit.model.PatternStop

object PatternStopMapper {

    fun buildFrom(response: StopsByRadiusQuery.Pattern): List<PatternStop>? {

        val thisMap: List<PatternStop>? = response.stops?.map { stop ->
            PatternStop(
                gtfsId = stop.gtfsId,
                code = stop.code,
                name = stop.name
            )
        }
        return thisMap
    }
}