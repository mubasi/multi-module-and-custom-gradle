package id.bluebird.vsm.feature.home.queue_ticket


sealed class QueueTicketState {
    object ProgressGetUser : QueueTicketState()
    object GetUserInfoSuccess : QueueTicketState()
    object ProsesTicket : QueueTicketState()
    data class FailedGetUser(val message: String) : QueueTicketState()
    data class FailedGetTicket(val message: String) : QueueTicketState()

}
