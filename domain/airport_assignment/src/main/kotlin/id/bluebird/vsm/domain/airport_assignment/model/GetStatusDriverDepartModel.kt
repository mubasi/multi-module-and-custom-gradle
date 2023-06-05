package id.bluebird.vsm.domain.airport_assignment.model

import androidx.annotation.Keep

@Keep
data class GetStatusDriverDepartModel(
    val isQueue : Boolean,
    val stockId : Long,
    val locationId : Long,
    val subLocationId : Long,
    val subLocationName : String,
    val locationName : String
)