package id.bluebird.mall.feature_queue_fleet.main

import id.bluebird.mall.feature_queue_fleet.model.FleetItem

sealed class QueueFleetState {
    object Idle : QueueFleetState()
    object ProgressGetUser : QueueFleetState()
    object ProgressGetFleetList : QueueFleetState()
    object GetUserInfoSuccess : QueueFleetState()
    object GetListEmpty : QueueFleetState()
    data class SearchFleet(val subLocationId: Long, val list: List<FleetItem>) : QueueFleetState()
    data class AddFleetSuccess(val list: List<FleetItem>) : QueueFleetState()
    data class AddFleet(val subLocationId: Long) : QueueFleetState()
    data class ShowRequestFleet(val subLocationId: Long) : QueueFleetState()
    data class FailedGetUser(val message: String) : QueueFleetState()
    data class FailedGetCounter(val message: String) : QueueFleetState()
    data class GetListSuccess(val list: List<FleetItem>) : QueueFleetState()
    data class FailedGetList(val throwable: Throwable) : QueueFleetState()
}