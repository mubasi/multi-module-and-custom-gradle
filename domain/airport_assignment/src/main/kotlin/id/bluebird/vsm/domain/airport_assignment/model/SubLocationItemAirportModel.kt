package id.bluebird.vsm.domain.airport_assignment.model

import androidx.annotation.Keep

@Keep
data class SubLocationItemAirportModel(
    val subLocationId : Long = -1,
    val subLocationName : String = "",
    val count : Long = -1,
)
