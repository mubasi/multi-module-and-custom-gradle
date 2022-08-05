package id.bluebird.mall.feature_queue_fleet.depart_fleet

import id.bluebird.mall.feature_queue_fleet.model.FleetItem

sealed class DepartFleetState {
    object CancelDepart: DepartFleetState()
    data class SelectQueueToDepart(val fleetItem: FleetItem, val currentQueueId: String): DepartFleetState()
    data class DepartFleet(val fleetItem: FleetItem, val isWithPassenger: Boolean, val currentQueueNumber: String): DepartFleetState()
}
