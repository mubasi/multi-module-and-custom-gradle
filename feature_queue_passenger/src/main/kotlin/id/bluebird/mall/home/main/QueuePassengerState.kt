package id.bluebird.mall.home.main


sealed class QueuePassengerState {
    object ProsesQueue : QueuePassengerState()
    object ProsesSkipQueue : QueuePassengerState()

    object ProsesGetUser : QueuePassengerState()
    object SuccessGetUser : QueuePassengerState()
    data class FailedGetUser(val message: String) : QueuePassengerState()

    object ProsesCurrentQueue : QueuePassengerState()
    object SuccessCurrentQueue : QueuePassengerState()
    data class FailedCurrentQueue(val message: String) : QueuePassengerState()

}