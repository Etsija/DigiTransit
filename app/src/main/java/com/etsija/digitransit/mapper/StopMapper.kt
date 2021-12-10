package com.etsija.digitransit.mapper

import com.etsija.digitransit.StopsByRadiusQuery
import com.etsija.digitransit.model.Stop

object StopMapper {

    fun buildFrom(response: StopsByRadiusQuery.Edge): Stop {

        // Set info card color based on stop type. If type cannot be parsed directly,
        // it is returned as the original numeric code to be further parsed
        val vehicleTypeAsString = when (response.node?.stop?.vehicleType) {
            0 -> "TRAM"
            1 -> "SUBWAY"
            2, 109 -> "RAIL"
            3 -> "BUS"
            else -> response.node?.stop?.vehicleType.toString()
        }

        return Stop(
            response.node?.stop!!.id,
            response.node.stop.gtfsId,
            response.node.stop.name,
            response.node.stop.code,
            response.node.stop.zoneId,
            response.node.distance,
            PatternMapper.buildFrom(response.node),
            vehicleTypeAsString
        )
    }

    }
