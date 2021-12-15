package com.etsija.digitransit.model

data class Pattern(
    val name: String = "",
    val direction: String? = "",
    val patternStops: List<PatternStop?>?
)
