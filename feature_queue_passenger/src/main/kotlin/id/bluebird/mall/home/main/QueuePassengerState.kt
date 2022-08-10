package id.bluebird.mall.home.main
import id.bluebird.mall.home.model.QueueReceiptCache

sealed class QueuePassengerState {
    object ProsesQueue : QueuePassengerState()
    object ProsesSkipQueue : QueuePassengerState()

    object ProsesGetUser : QueuePassengerState()
    object SuccessGetUser : QueuePassengerState()
    data class FailedGetUser(val message: String) : QueuePassengerState()

    object ProsesCurrentQueue : QueuePassengerState()
    object SuccessCurrentQueue : QueuePassengerState()
    data class FailedCurrentQueue(val message: String) : QueuePassengerState()

    object ProsesListQueue : QueuePassengerState()
    object SuccessListQueue : QueuePassengerState()
    data class FailedListQueue(val message: String) : QueuePassengerState()

    object ProsesListQueueSkipped : QueuePassengerState()
    object SuccessListQueueSkipped : QueuePassengerState()
    data class FailedListQueueSkipped(val message: String) : QueuePassengerState()

    data class ProsesDeleteQueueSkipped(val queueReceiptCache: QueueReceiptCache) : QueuePassengerState()
    data class ProsesRestoreQueueSkipped(val queueReceiptCache: QueueReceiptCache) : QueuePassengerState()
    object SuccessDeleteQueueSkipped : QueuePassengerState()
    data class FailedDeleteQueueSkipped(val message: String) : QueuePassengerState()

}