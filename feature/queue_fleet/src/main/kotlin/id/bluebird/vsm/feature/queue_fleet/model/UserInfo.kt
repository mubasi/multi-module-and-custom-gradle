package id.bluebird.vsm.feature.queue_fleet.model

import androidx.annotation.Keep

@Keep
data class UserInfo(val userId: Long = 0, var locationId: Long = -1, var subLocationId: Long = -1)