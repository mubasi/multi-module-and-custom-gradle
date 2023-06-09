package id.bluebird.vsm.fleet_non_apsh.main.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationAssignmentModel(
    val id : Long = -1,
    val name : String = "",
    val isPerimeter : Boolean = false,
    val isWings : Boolean = false
) : Parcelable