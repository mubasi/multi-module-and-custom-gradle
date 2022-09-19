package id.bluebird.vsm.domain.fleet.model

import androidx.annotation.Keep

@Keep
data class FleetDepartResult(
    val taxiNo: String,
    val message: String,
    val stockType: String,
    val stockId: String,
    val createdAt: String
)