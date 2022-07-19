package id.bluebird.mall.feature_queue_fleet.model

import androidx.annotation.Keep

@Keep
data class CountCache(
    var isInit: Boolean = false,
    var stock: Long = 0,
    var request: Long = 0,
    var ritase: Long = 0
)
