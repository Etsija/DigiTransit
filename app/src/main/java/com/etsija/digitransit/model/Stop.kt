package com.etsija.digitransit.model

data class Stop(
    val gtfsId: String = "",
    val stopName: String = "",
    val distance: Int?
) {

}