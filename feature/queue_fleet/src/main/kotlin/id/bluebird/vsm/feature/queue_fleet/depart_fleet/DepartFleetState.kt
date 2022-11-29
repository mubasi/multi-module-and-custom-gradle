package id.bluebird.vsm.feature.queue_fleet.depart_fleet

import id.bluebird.vsm.feature.queue_fleet.model.FleetItem

sealed class DepartFleetState {
    object CancelDepart: DepartFleetState()
    object OnProgressGetCurrentQueue: DepartFleetState()
    data class OnFailed(val throwable: Throwable): DepartFleetState()
    data class SelectQueueToDepart(val fleetItem: FleetItem, val currentQueueId: String, val locationId: Long, val subLocationId: Long): DepartFleetState()
    data class DepartFleet(val fleetItem: FleetItem, val isWithPassenger: Boolean, val currentQueueNumber: String): DepartFleetState()
}
