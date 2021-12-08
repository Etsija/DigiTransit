package com.etsija.digitransit.mapper

import com.etsija.digitransit.StopsByRadiusQuery
import com.etsija.digitransit.model.Pattern

object PatternMapper {

    fun buildFrom(response: StopsByRadiusQuery.Node): List<Pattern?>? {

        val thisMap: List<Pattern?>? = response.stop?.patterns?.map { pattern ->
            Pattern(pattern!!.id, pattern.name!!)
        }
        return thisMap

    }
}