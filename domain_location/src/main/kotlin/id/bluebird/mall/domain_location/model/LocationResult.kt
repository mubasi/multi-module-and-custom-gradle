package id.bluebird.mall.domain_location.model

import androidx.annotation.Keep

@Keep
data class LocationResult(
    val id: Long,
    val locationName: String,
    val isActive: Boolean,
    val codeArea: String,
)