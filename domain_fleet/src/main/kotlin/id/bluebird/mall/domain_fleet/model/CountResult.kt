package id.bluebird.mall.domain_fleet.model

import androidx.annotation.Keep

@Keep
data class CountResult(val stock: Long, val ritase: Long, val request: Long = -1)
