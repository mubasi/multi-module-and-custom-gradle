package id.bluebird.mall.domain_pasenger.model

import androidx.annotation.Keep

@Keep
data class CurrentQueueResult(
    val id: Long,
    val number: String,
    val createdAt: String
)