package id.bluebird.mall.home.model

import androidx.annotation.Keep

@Keep
data class SearchQueueCache(
    val search_type: String = "",
    val queues: ArrayList<QueueReceiptCache>
)