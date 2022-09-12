package id.bluebird.vsm.feature.user_management.create.model

import androidx.annotation.Keep

@Keep
data class RoleCache(val id: Long = -1, val name: String = "") {
    override fun toString(): String = name
}
