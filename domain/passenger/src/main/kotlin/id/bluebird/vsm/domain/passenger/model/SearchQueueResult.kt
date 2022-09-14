package id.bluebird.vsm.domain.passenger.model

import androidx.annotation.Keep

@Keep
data class SearchQueueResult(
    val search_type: String,
    val queues: ArrayList<Queue>
)