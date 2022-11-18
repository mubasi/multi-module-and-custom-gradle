package id.bluebird.vsm.domain.passenger.model

import androidx.annotation.Keep

@Keep
data class SearchQueueResult(
    val searchType: String,
    val queues: ArrayList<Queue>
)