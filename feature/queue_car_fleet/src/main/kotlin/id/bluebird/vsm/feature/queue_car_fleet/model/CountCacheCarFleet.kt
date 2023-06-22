package id.bluebird.vsm.feature.queue_car_fleet.model

import androidx.annotation.Keep

@Keep
data class CountCacheCarFleet(
    var isInit: Boolean = false,
    var stock: Long = 0,
    var request: Long = 0,
    var ritase: Long = 0,
    val depositionStock : Long = 0
)
