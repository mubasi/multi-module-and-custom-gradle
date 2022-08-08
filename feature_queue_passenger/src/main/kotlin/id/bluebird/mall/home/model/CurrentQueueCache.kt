package id.bluebird.mall.home.model

import androidx.annotation.Keep

@Keep
data class CurrentQueueCache(
    val id: Long = -1,
    val number: String = "",
    val createdAt: String = ""
)