package id.bluebird.vsm.feature.select_location.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CacheParentModel(
    val id: Long,
    val name: String,
    var isExpanded: Boolean = false,
    var type: Int = PARENT
) : Parcelable {
    companion object {
        const val PARENT = 1
        const val CHILD = 2
    }
}