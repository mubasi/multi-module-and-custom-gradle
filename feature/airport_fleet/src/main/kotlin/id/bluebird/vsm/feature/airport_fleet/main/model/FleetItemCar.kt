package id.bluebird.vsm.feature.airport_fleet.main.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class FleetItemCar(
    var id: Long = -2, val name: String = "", val arriveAt: String = "",
    var isSelected : Boolean = false, var status : String = ""
) : Parcelable