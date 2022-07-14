package id.bluebird.mall.home.model

import androidx.annotation.Keep

@Keep
data class QueueReceiptCache (
    var queueId: Long = 0,
    var queueNumber : String = ""
)
