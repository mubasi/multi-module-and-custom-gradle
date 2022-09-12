package id.bluebird.vsm.feature.home.model

import androidx.annotation.Keep

@Keep
data class ListQueueResultCache(
    val count: Long = 0,
    val queue: ArrayList<QueueReceiptCache>
)