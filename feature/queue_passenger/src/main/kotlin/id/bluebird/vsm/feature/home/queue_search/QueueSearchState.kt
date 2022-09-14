package id.bluebird.vsm.feature.home.queue_search

import id.bluebird.vsm.feature.home.model.QueueReceiptCache

sealed class QueueSearchState {
    object ProsesSearchQueue : QueueSearchState()
    object SuccessSearchQueue: QueueSearchState()
    object ClearSearchQueue: QueueSearchState()
    data class FailedSearchQueue(val message: String) : QueueSearchState()

    data class ProsesDeleteQueueSkipped(val queueReceiptCache: QueueReceiptCache) : QueueSearchState()
    data class ProsesRestoreQueueSkipped(val queueReceiptCache: QueueReceiptCache) : QueueSearchState()
}