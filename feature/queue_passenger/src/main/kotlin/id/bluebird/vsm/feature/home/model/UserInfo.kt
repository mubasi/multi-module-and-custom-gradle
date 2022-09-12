package id.bluebird.vsm.feature.home.model

import androidx.annotation.Keep

@Keep
data class UserInfo(val userId: Long = 0, var locationId: Long = -1, var subLocationId: Long = -1)