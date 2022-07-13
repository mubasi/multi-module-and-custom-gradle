package id.bluebird.mall.feature_queue_fleet.request_fleet

sealed class RequestFleetDialogState {
    object CancelDialog : RequestFleetDialogState()
    object Idle : RequestFleetDialogState()
    data class FocusState(val isFocus: Boolean) : RequestFleetDialogState()
    data class MessageError(val message: String) : RequestFleetDialogState()
    data class Err(val err: Throwable) : RequestFleetDialogState()
    data class RequestSuccess(val count: Long) : RequestFleetDialogState()

}
