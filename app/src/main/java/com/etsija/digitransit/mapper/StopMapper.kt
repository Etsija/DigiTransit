package com.etsija.digitransit.mapper

import android.graphics.Color
import com.etsija.digitransit.AlertsQuery
import com.etsija.digitransit.StopsByRadiusQuery
import com.etsija.digitransit.model.Alert
import com.etsija.digitransit.model.Stop
import com.etsija.digitransit.utils.Helpers

object StopMapper {

    fun buildFrom(response: StopsByRadiusQuery.Edge): Stop {

        // Set info card color based on alert severity
        val vehicleTypeAsString = when (response.node?.stop!!.vehicleType) {
            0 -> "TRAM"
            1 -> "SUBWAY"
            2 -> "RAIL"
            3 -> "BUS"
            else -> "UNKNOWN"
        }

        return Stop(
            response.node?.stop!!.gtfsId,
            response.node?.stop!!.name,
            response.node?.distance!!,
            vehicleTypeAsString
        )
    }
    }
