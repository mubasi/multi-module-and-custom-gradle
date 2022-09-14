package id.bluebird.vsm.domain.passenger.model

import androidx.annotation.Keep

@Keep
data class ListQueueResult(
    val count: Long,
    val queue: ArrayList<Queue>
)