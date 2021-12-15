package com.etsija.digitransit.mapper

import com.etsija.digitransit.StopsByRadiusQuery
import com.etsija.digitransit.model.Pattern
import com.etsija.digitransit.utils.Helpers.Companion.tidyPatternName

object PatternMapper {

    fun directionAsString(direction: Int?): String {
        return when (direction) {
            1 -> "INBOUND"
            0 -> "OUTBOUND"
            else -> "UNDEFINED"
        }
    }

    fun buildFrom(response: StopsByRadiusQuery.Node): List<Pattern?>? {

        val thisMap: List<Pattern?>? = response.stop?.patterns?.map { pattern ->
            Pattern(
                name = tidyPatternName(pattern?.name!!),
                direction = directionAsString(pattern.directionId),
                patternStops = pattern.let { PatternStopMapper.buildFrom(it) }
            )
        }
        return thisMap
    }
}