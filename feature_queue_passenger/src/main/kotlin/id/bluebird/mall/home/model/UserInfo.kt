package id.bluebird.mall.home.model

import androidx.annotation.Keep

@Keep
data class UserInfo(val userId: Long, var locationId: Long = -1, var subLocationId: Long = -1)