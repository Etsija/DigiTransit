package com.etsija.digitransit.mapper

import com.etsija.digitransit.StopsByRadiusQuery
import com.etsija.digitransit.model.Stop

object StopMapper {

    fun buildFrom(response: StopsByRadiusQuery.Node): Stop {

        // Set info card color based on stop type. If type cannot be parsed directly,
        // it is returned as the original numeric code to be further parsed
        val vehicleTypeAsString = when (response.stop?.vehicleType) {
            0 -> "TRAM"
            1 -> "METRO"
            2, 109 -> "RAIL"
            3 -> "BUS"
            else -> response.stop?.vehicleType.toString()
        }

        return Stop(
            gtfsId = response.stop?.gtfsId,
            stopName = response.stop?.name,
            stopCode = response.stop?.code,
            zoneId = response.stop?.zoneId,
            parentName = response.stop?.parentStation?.name,
            distance = response.distance,
            patterns = response.let { PatternMapper.buildFrom(it) },
            type = vehicleTypeAsString
        )
    }
}
