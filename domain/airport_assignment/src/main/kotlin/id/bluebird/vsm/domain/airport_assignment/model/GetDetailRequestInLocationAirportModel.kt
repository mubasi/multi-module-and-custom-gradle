package id.bluebird.vsm.domain.airport_assignment.model

import androidx.annotation.Keep

@Keep
data class GetDetailRequestInLocationAirportModel(
    val subLocationItem: ArrayList<SubLocationItemAirportModel>,
)
