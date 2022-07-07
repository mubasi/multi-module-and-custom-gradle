package id.bluebird.mall.feature_user_management.create.model

import androidx.annotation.Keep

@Keep
data class UserCache(
    val id: Long? = null,
    var name: String = "",
    var username: String = "",
    var email: String = "",
    var userRole: RoleCache? = null,
    var userAssignment: List<LocationAssignment> = ArrayList()
) {

    constructor() : this(null, "", "", "")

    lateinit var password: String
    var newPassword: String? = null
    var createAt: String? = null
    var modifiedAt: String? = null
    var createBy: Long? = null
    var modifiedBy: Long? = null
}
