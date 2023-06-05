package id.bluebird.vsm.feature.queue_car_fleet.depart_fleet

import id.bluebird.vsm.feature.queue_car_fleet.model.CarFleetItem

sealed class DepartCarFleetState {
    object CancelDepartCar: DepartCarFleetState()
    object OnProgressGetCurrentQueue: DepartCarFleetState()
    data class OnFailed(val throwable: Throwable): DepartCarFleetState()
    data class OnFailedGetCurrentQueue(val throwable: Throwable): DepartCarFleetState()
    data class SuccessGetCurrentQueue(val queueId: String): DepartCarFleetState()
    data class SelectQueueToDepartCar(val carFleetItem: CarFleetItem, val currentQueueId: String, val locationId: Long, val subLocationId: Long): DepartCarFleetState()
    data class DepartCarFleet(val carFleetItem: CarFleetItem, val isWithPassenger: Boolean, val currentQueueNumber: String): DepartCarFleetState()
}
