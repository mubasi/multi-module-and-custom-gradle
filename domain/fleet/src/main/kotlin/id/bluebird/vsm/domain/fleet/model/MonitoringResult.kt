package id.bluebird.vsm.domain.fleet.model

import androidx.annotation.Keep

@Keep
data class MonitoringResult(
    var buffer: Int = 0,
    var locationName: String = "",
    var subLocationName: String = "",
    var queueFleet: Int = 0,
    var queuePassenger: Int = 0,
    var request: Int = 0,
    var subLocationId: Long = 0,
    var totalQueueFleet: Int = 0,
    var totalQueuePassenger: Int = 0,
    var totalRitase: Int = 0
)
