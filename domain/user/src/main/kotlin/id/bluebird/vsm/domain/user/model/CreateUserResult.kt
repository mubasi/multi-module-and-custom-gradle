package id.bluebird.vsm.domain.user.model

import androidx.annotation.Keep

@Keep
data class CreateUserResult(
    val id: Long = 0,
    val name: String = "",
    val username: String = "",
    val roleId: Long = -1,
    val locationId: Long = -1,
    val locationName: String = "",
    val subLocationsId: List<Long> = ArrayList(),
    val subLocationName: String = "",
    val prefix: String = "",
)