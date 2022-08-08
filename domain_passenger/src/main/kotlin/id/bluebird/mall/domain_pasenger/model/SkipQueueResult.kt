package id.bluebird.mall.domain_pasenger.model

import androidx.annotation.Keep

@Keep
data class SkipQueueResult(
    val skippedId: Long,
    val nextQueue: Queue
)