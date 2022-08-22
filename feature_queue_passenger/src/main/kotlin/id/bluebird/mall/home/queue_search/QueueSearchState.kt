package id.bluebird.mall.home.queue_search

import id.bluebird.mall.home.model.QueueReceiptCache

sealed class QueueSearchState {
    object ProsesSearchQueue : QueueSearchState()
    object SuccessSearchQueue: QueueSearchState()
    object ClearSearchQueue: QueueSearchState()
    data class FailedSearchQueue(val message: String) : QueueSearchState()

    data class ProsesDeleteQueueSkipped(val queueReceiptCache: QueueReceiptCache) : QueueSearchState()
    data class ProsesRestoreQueueSkipped(val queueReceiptCache: QueueReceiptCache) : QueueSearchState()
}