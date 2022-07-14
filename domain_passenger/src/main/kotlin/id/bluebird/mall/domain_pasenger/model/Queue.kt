package id.bluebird.mall.domain_pasenger.model

import androidx.annotation.Keep

@Keep
data class Queue (
    val id : Long,
    val number: String,
    val createdAt: String,
    val message: String,
    val currentQueue: String,
    val totalQueue: Long,
    val timeOrder: String,
    val subLocationId: Long
)