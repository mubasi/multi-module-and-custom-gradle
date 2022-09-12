package id.bluebird.vsm.feature.home.model

import androidx.annotation.Keep

@Keep
data class CounterBarCache(
    val locationId : Long = 0,
    val ongoing: Long = 0,
    val skipped: Long = 0,
    val ritese: Long = 0,
    val modifiedAt: String = "",
)