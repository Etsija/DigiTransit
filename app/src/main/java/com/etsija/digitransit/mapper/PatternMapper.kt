package com.etsija.digitransit.mapper

import com.etsija.digitransit.StopsByRadiusQuery
import com.etsija.digitransit.model.Pattern
import com.etsija.digitransit.utils.Helpers.Companion.tidyPatternName

object PatternMapper {

    fun buildFrom(response: StopsByRadiusQuery.Node): List<Pattern?>? {

        val thisMap: List<Pattern?>? = response.stop?.patterns?.map { pattern ->
            Pattern(pattern!!.id, tidyPatternName(pattern.name!!))
        }
        return thisMap

    }
}