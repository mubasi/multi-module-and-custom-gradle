package id.bluebird.vsm.domain.airport_assignment.model

import androidx.annotation.Keep

@Keep
data class GetRequestDepartSubLocationModel(
    val subLocationName : String,
    val count : Long,
    val subLocationId : Long,
    val withPassenger : Boolean
)
