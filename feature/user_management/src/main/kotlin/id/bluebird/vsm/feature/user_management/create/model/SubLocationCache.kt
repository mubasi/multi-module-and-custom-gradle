package id.bluebird.vsm.feature.user_management.create.model

import androidx.annotation.Keep

@Keep
data class SubLocationCache(
    val id: Long,
    val locationId: Long,
    val name: String,
    var isSelected: Boolean = false
) {
    override fun toString(): String = name
}
