package id.bluebird.mall.domain.user.model

import androidx.annotation.Keep

@Keep
data class CreateUserResult(
    val id: Long = 0,
    val name: String,
    val username: String,
    val roleId: Long = -1,
    val locationId: Long,
    val locationName:String,
    val subLocationsId: List<Long> = ArrayList(),
    val subLocationName:String = ""
)