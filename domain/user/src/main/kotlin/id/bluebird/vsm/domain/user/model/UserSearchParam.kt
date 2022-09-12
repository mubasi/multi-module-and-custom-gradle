package id.bluebird.vsm.domain.user.model

import androidx.annotation.Keep

@Keep
data class UserSearchParam(
    val id: Long,
    val username: String,
    val uuid: String,
    val status: Boolean
) {
    companion object {
        fun convertStatusInfo(info: String): Boolean = info == "Active"
    }
}