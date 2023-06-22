package id.bluebird.vsm.feature.queue_car_fleet.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class CarFleetItem(val id: Long = -2, val name: String = "", val arriveAt: String = "", val sequence : Long = -1) :
    Parcelable
