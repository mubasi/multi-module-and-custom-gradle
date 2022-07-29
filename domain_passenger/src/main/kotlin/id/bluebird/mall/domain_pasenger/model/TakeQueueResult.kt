package id.bluebird.mall.domain_pasenger.model

import androidx.annotation.Keep

@Keep
data class TakeQueueResult(
    val message: String,
    val queueType: String,
    val queue: Queue
)
