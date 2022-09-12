package id.bluebird.vsm.feature.home.model

import androidx.annotation.Keep

@Keep
data class CounterModel(var success: Long = 0, var delay: Long = 0, var queue: Long = 0)
