package id.bluebird.vsm.feature.user_management.list

import androidx.annotation.Keep

@Keep
data class UserSettingCache(
    val id: Long? = null,
    val uuid: String,
    var userName: String,
    var status: Boolean = false
)
