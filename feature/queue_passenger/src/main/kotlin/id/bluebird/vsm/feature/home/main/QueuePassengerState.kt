package id.bluebird.vsm.feature.home.main
import id.bluebird.vsm.feature.home.model.QueueReceiptCache

sealed class QueuePassengerState {
    object ProgressHolder: QueuePassengerState()
    object ToSelectLocation: QueuePassengerState()

    object ProsesQueue : QueuePassengerState()
    object ProsesSkipQueue : QueuePassengerState()
    object ProsesRitase : QueuePassengerState()

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

    object ProsesCounterBar : QueuePassengerState()
    object SuccessCounterBar : QueuePassengerState()
    data class FailedCounterBar(val message: String) : QueuePassengerState()

    data class ToSearchQueue(
        val locationId: Long,
        val subLocationId: Long,
        val prefix: String,
        val listWaiting: ArrayList<QueueReceiptCache>,
        val listSkipped: ArrayList<QueueReceiptCache>
    ) : QueuePassengerState()

    data class ToQrCodeScreen(
        val locationId: Long,
        val subLocationId: Long,
        val titleLocation: String
    ) : QueuePassengerState()

}