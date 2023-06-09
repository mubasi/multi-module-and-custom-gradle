package id.bluebird.vsm.domain.airport_assignment.model

import androidx.annotation.Keep

@Keep
data class ArrivedItemModel(
    val stockId : Long,
    val taxiNo : String,
    val createdAt : String
)
