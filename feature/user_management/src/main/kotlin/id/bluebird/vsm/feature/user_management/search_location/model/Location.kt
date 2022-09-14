package id.bluebird.vsm.feature.user_management.search_location.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Location(
    val id: Long,
    val name: String,
    var isSelected: Boolean = false
): Parcelable
