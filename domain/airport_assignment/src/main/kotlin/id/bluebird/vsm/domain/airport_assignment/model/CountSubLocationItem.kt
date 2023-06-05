package id.bluebird.vsm.domain.airport_assignment.model

import androidx.annotation.Keep

@Keep
data class CountSubLocationItem(
    val subLocationName : String = "",
    val count : Long = -1,
    val subLocationId : Long = -1,
    val withPassenger : Boolean = true
)
