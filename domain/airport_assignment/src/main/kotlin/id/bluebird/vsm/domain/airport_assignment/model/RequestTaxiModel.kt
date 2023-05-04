package id.bluebird.vsm.domain.airport_assignment.model

import androidx.annotation.Keep

@Keep
data class RequestTaxiModel(
    val message : String,
    val requestFrom : Long,
    val createdAt : String,
    val requestCount : Long
)
