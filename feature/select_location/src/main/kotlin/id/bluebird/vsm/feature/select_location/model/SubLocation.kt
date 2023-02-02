package id.bluebird.vsm.feature.select_location.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SubLocation(
    val id: Long,
    val name: String,
    val locationId: Long,
    val locationName: String,
    var type: Int = LocationModel.CHILD
) : Parcelable