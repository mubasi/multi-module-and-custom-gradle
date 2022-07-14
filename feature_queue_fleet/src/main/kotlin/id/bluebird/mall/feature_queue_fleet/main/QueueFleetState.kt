package id.bluebird.mall.feature_queue_fleet.main

sealed class QueueFleetState {
    object Idle : QueueFleetState()
    object ProgressGetUser : QueueFleetState()
    object GetUserInfoSuccess : QueueFleetState()
    data class AddFleet(val subLocationId: Long) : QueueFleetState()
    data class ShowRequestFleet(val subLocationId: Long) : QueueFleetState()
    data class FailedGetUser(val message: String) : QueueFleetState()
    data class FailedGetCounter(val message: String) : QueueFleetState()
}