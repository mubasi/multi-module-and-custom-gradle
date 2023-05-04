package id.bluebird.vsm.domain.airport_assignment.model

import androidx.annotation.Keep

@Keep
data class AssignFleetModel(
    val subLocationId: Long = -1,
    val locationId: Long = -1,
    val withPassenger: Boolean = false,
    val isArrived: Boolean = false,
    var carsAssignment: List<FleetItemDepartModel> = ArrayList()
)
