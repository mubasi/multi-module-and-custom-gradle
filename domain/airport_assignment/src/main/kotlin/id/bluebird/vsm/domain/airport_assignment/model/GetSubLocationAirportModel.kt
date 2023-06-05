package id.bluebird.vsm.domain.airport_assignment.model

import androidx.annotation.Keep

@Keep
data class GetSubLocationAirportModel(
    val locationName : String = "",
    val locationId : Long = -1,
    val countSubLocationItem : ArrayList<CountSubLocationItem>
)
