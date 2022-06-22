package id.bluebird.mall.domain.user.model

import androidx.annotation.Keep

@Keep
data class LoginParam(val username: String, val password: String)
