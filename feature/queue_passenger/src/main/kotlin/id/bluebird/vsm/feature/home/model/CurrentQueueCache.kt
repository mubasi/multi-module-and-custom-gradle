package id.bluebird.vsm.feature.home.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class CurrentQueueCache(
    val id: Long = -1,
    val number: String = "",
    val createdAt: String = ""
) : Parcelable