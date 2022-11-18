package id.bluebird.vsm.feature.home.model

import androidx.annotation.Keep

@Keep
data class SearchQueueCache(
    val searchType: String = "",
    val queues: ArrayList<QueueReceiptCache>
)