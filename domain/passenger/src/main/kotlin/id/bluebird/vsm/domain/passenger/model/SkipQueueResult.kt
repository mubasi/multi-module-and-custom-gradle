package id.bluebird.vsm.domain.passenger.model

import androidx.annotation.Keep

@Keep
data class SkipQueueResult(
    val skippedId: Long,
    val nextQueue: Queue
)