package id.bluebird.vsm.feature.home.model

import androidx.annotation.Keep

@Keep
data class QueueSearchCache(
    var queueId: Long = 0,
    var queueNumber: String = "",
    var isWaiting: Boolean = true
)
