package id.bluebird.mall.domain_fleet.model

import androidx.annotation.Keep

@Keep
data class AssignFleetsParam(
    val subLocationId: Long = -1,
    val locationId: Long = -1,
    val withPassenger: Boolean = false,
    val isArrived: Boolean = false,
    var fleetStockId: List<Long> = ArrayList()
)
