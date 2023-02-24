package id.bluebird.vsm.feature.home.model

import androidx.annotation.Keep

@Keep
data class FleetItemList(
    val id: Long = -2,
    val name: String = "",
    val arriveAt: String = "",
    var isSelected : Boolean = false
)