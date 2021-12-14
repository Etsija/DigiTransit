package com.etsija.digitransit.mapper

import com.etsija.digitransit.StopsByRadiusQuery
import com.etsija.digitransit.model.Pattern
import com.etsija.digitransit.utils.Helpers.Companion.tidyPatternName

object PatternMapper {

    fun buildFrom(response: StopsByRadiusQuery.Node): List<Pattern?>? {

        val thisMap: List<Pattern?>? = response.stop?.patterns?.map { pattern ->
            Pattern(
                name = tidyPatternName(pattern?.name!!),
                patternStops = pattern.let { PatternStopMapper.buildFrom(it) }
            )
        }
        return thisMap
    }
}