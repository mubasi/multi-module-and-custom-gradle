package id.bluebird.mall.officer.ui.home

import androidx.annotation.Keep

@Keep
data class CounterModel(var success: Long = 0, var delay: Long = 0, var queue: Long = 0)
