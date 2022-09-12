package id.bluebird.vsm.feature.home.model

import androidx.annotation.Keep

@Keep
data class TakeQueueCache(
    var queueId: Long = 0,
    var queueNumber: String = "",
    var createdAt: String = "",
    var message: String = "",
    var currentQueue: String = "",
    var totalQueue: Long = 0,
    var subLocationId: Long = 0
)