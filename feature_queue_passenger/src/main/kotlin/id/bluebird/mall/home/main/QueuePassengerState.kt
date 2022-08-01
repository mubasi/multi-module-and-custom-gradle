package id.bluebird.mall.home.main


sealed class QueuePassengerState {
    object ProsesQueue : QueuePassengerState()
    data class FailedGetQueue(val message: String) : QueuePassengerState()
}