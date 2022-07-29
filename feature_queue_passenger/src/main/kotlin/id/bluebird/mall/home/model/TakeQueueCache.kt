package id.bluebird.mall.home.model

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