package id.bluebird.vsm.feature.select_location.model

import androidx.annotation.Keep

@Keep
data class SubLocationModelCache(
    val id: Long,
    val name: String,
    val prefix: String,
    val isPerimeter : Boolean,
    val isWing : Boolean,
    var type: Int = LocationModel.CHILD
)
