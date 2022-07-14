package id.bluebird.mall.home.dialog_queue_receipt

sealed class DialogQueueReceiptState {
    object CancelDialog : DialogQueueReceiptState()
    object ProsesQueue : DialogQueueReceiptState()
    object ProgressGetQueue : DialogQueueReceiptState()
    object GetQueueSuccess : DialogQueueReceiptState()
    object ProgressGetUser : DialogQueueReceiptState()
    object GetUserInfoSuccess : DialogQueueReceiptState()
    object TakeQueueSuccess : DialogQueueReceiptState()
    data class MessageError(val message: String) : DialogQueueReceiptState()
    data class Err(val err: Throwable) : DialogQueueReceiptState()
    data class FailedGetQueue(val message: String) : DialogQueueReceiptState()
    data class FailedGetUser(val message: String) : DialogQueueReceiptState()
    data class FailedTakeQueue(val message: String) : DialogQueueReceiptState()
}