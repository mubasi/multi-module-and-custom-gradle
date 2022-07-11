package id.bluebird.mall.feature_queue_fleet.main

sealed class QueueFleetState {
    object Idle : QueueFleetState()
    object ShowAddDialog : QueueFleetState()
    object ProgressGetUser : QueueFleetState()
    object GetUserInfoSuccess : QueueFleetState()
    data class FailedGetUser(val message: String) : QueueFleetState()
    data class FailedGetCounter(val message: String) : QueueFleetState()
}