package id.bluebird.mall.home.model

import androidx.annotation.Keep

@Keep
data class ListQueueResultCache(
    val count: Long = 0,
    val queue: ArrayList<QueueReceiptCache>
)