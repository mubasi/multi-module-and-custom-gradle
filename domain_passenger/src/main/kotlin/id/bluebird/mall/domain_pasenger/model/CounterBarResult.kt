package id.bluebird.mall.domain_pasenger.model

import androidx.annotation.Keep

@Keep
data class CounterBarResult(
    val locationId : Long,
    val ongoing: Long,
    val skipped: Long,
    val ritese: Long,
    val modifiedAt: String,
)