package id.bluebird.vsm.domain.passenger.model

import androidx.annotation.Keep

@Keep
data class TakeQueueResult(
    val message: String,
    val queueType: String,
    val queue: Queue
)
