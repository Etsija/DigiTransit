package com.etsija.digitransit.mapper

import com.etsija.digitransit.StopsByRadiusQuery
import com.etsija.digitransit.model.Stop

object StopMapper {

    fun buildFrom(response: StopsByRadiusQuery.Edge): Stop {

        // Set info card color based on stop type. If type cannot be parsed directly,
        // it is returned as the original numeric code to be further parsed
        val vehicleTypeAsString = when (response.node?.stop?.vehicleType) {
            0 -> "TRAM"
            1 -> "METRO"
            2, 109 -> "RAIL"
            3 -> "BUS"
            else -> response.node?.stop?.vehicleType.toString()
        }

        return Stop(
            gtfsId = response.node?.stop?.gtfsId,
            stopName = response.node?.stop?.name,
            stopCode = response.node?.stop?.code,
            zoneId = response.node?.stop?.zoneId,
            parentName = response.node?.stop?.parentStation?.name,
            distance = response.node?.distance,
            patterns = response.node?.let { PatternMapper.buildFrom(it) },
            type = vehicleTypeAsString
        )
    }
}
