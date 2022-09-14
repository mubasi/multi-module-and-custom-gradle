package id.bluebird.vsm.domain.location.model

import androidx.annotation.Keep

@Keep
data class LocationResult(
    val id: Long,
    val locationName: String,
    val isActive: Boolean,
    val codeArea: String,
)