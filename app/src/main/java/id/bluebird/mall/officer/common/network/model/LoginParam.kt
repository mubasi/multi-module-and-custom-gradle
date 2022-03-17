package id.bluebird.mall.officer.common.network.model

import androidx.annotation.Keep

@Keep
data class LoginParam(val name: String = "", val password: String = "")
