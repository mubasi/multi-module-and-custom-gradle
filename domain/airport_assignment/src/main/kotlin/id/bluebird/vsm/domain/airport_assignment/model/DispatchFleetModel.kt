package id.bluebird.vsm.domain.airport_assignment.model

import androidx.annotation.Keep

@Keep
data class DispatchFleetModel(
    val subLocationId: Long = -1,
    val locationId: Long = -1,
    val isPerimeter: Boolean = false,
    val withPassenger: Boolean = false,
    val isArrived: Boolean = false,
    var fleetsAssignment: List<Long> = ArrayList()
)
