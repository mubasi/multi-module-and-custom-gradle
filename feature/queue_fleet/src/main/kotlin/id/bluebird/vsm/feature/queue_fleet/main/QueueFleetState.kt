package id.bluebird.vsm.feature.queue_fleet.main

import id.bluebird.vsm.feature.queue_fleet.model.FleetItem

sealed class QueueFleetState {
    object ProgressHolder:QueueFleetState()
    object ToSelectLocation : QueueFleetState()
    object Idle : QueueFleetState()
    object ProgressGetUser : QueueFleetState()
    object ProgressGetFleetList : QueueFleetState()
    object ProgressDepartFleet : QueueFleetState()
    object GetUserInfoSuccess : QueueFleetState()
    object GetListEmpty : QueueFleetState()
    data class RecordRitaseToDepart(
        val fleet: FleetItem,
        val subLocationId: Long,
        val queueId: String
    ) : QueueFleetState()

    data class SearchFleet(val subLocationId: Long, val list: List<FleetItem>) : QueueFleetState()
    data class AddFleetSuccess(val list: List<FleetItem>) : QueueFleetState()
    data class AddFleet(val subLocationId: Long) : QueueFleetState()
    data class ShowRequestFleet(val subLocationId: Long) : QueueFleetState()
    data class FailedGetUser(val message: String) : QueueFleetState()
    data class FailedGetCounter(val message: String) : QueueFleetState()
    data class GetListSuccess(val list: List<FleetItem>) : QueueFleetState()
    data class FailedGetList(val throwable: Throwable) : QueueFleetState()
    data class RequestDepartFleet(val fleet: FleetItem) : QueueFleetState()
    data class FleetDeparted(val list: List<FleetItem>, val removedIndex: Int) : QueueFleetState()
    data class SuccessDepartFleet(val fleetNumber: String, val isWithPassenger: Boolean) :
        QueueFleetState()

    data class SearchQueueToDepart(
        val fleet: FleetItem,
        val locationId: Long,
        val subLocationId: Long,
        val currentQueueId: String
    ) : QueueFleetState()

    data class FailedDepart(val message: String) : QueueFleetState()
}