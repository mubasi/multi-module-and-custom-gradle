package id.bluebird.vsm.feature.airport_fleet.main.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssignmentCarCache(
    val fleetNumber: String = "",
    var date: String = "",
    var dateAfterConvert: String = "",
    var stockId: Long = -1,
    var isSelected: Boolean = false,
    var status: String = "",
    var isTU: Boolean = false,
    val sequence: Long = -1
) : Parcelable

enum class STATUS {
    OTW, ARRIVED
}
