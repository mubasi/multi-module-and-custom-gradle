package id.bluebird.vsm.domain.user.model

import androidx.annotation.Keep

@Keep
data class CreateUserParam(
    val id: Long = 0,
    val name: String? = null,
    val username: String? = null,
    val password: String = "",
    val newPassword: String? = null,
    val roleId: Long = -1,
    val locationId: Long,
    val subLocationsId: List<Long> = ArrayList()
)
