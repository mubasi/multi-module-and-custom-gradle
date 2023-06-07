package id.bluebird.vsm.feature.airport_fleet.request_list

import androidx.annotation.Keep

@Keep
data class FleetRequestDetail(
    val subLocationName: String,
    val requestCount: Int,
)
