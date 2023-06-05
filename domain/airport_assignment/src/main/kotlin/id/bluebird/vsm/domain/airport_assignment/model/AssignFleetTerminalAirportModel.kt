package id.bluebird.vsm.domain.airport_assignment.model

import androidx.annotation.Keep

@Keep
data class AssignFleetTerminalAirportModel(
    val message : String = "",
    val totalAssignedFleet : Long = -1
)
