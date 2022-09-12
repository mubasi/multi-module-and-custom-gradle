package id.bluebird.vsm.domain.passenger.model

import androidx.annotation.Keep

@Keep
data class CurrentQueue(
    val id: String,
    val number: String,
    val createdAt: String,
)
