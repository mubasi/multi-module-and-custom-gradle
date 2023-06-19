package id.bluebird.vsm.feature.select_location.model

import androidx.annotation.Keep

@Keep
data class SubLocation(
    val id: Long,
    val name: String,
    val locationId: Long,
    val locationName: String,
    val prefix: String,
    val haveDeposition: Boolean,
    val depositionId: Long,
    var type: Int = LocationModel.CHILD
)