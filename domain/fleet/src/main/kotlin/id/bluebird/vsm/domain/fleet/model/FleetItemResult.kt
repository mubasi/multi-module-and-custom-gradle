package id.bluebird.vsm.domain.fleet.model

import androidx.annotation.Keep

@Keep
data class FleetItemResult(val fleetId: Long, val fleetName: String, val arriveAt: String, val sequence : Long)
