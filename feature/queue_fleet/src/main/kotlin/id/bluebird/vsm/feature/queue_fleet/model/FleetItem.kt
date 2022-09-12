package id.bluebird.vsm.feature.queue_fleet.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class FleetItem(val id: Long = -2, val name: String = "", val arriveAt: String = "") :
    Parcelable
