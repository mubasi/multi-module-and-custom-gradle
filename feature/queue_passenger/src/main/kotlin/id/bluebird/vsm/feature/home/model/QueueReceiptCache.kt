package id.bluebird.vsm.feature.home.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class QueueReceiptCache(
    var queueId: Long = 0,
    var queueNumber: String = ""
) : Parcelable
