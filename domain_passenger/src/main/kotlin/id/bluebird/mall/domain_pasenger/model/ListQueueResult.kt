package id.bluebird.mall.domain_pasenger.model

import androidx.annotation.Keep

@Keep
data class ListQueueResult(
    val count: Long,
    val queue: ArrayList<Queue>
)