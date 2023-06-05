package id.bluebird.vsm.domain.airport_location.model

import androidx.annotation.Keep

@Keep
data class SubLocationItemModel(
    val subLocationId : Long,
    val subLocationName : String,
    val subLocationType : String,
    val isDeposition : Boolean,
    val isWings : Boolean
)
