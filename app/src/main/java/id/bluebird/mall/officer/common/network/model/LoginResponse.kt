package id.bluebird.mall.officer.common.network.model

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class LoginResponse(
    val id: String = "",
    @field:Json(name = "access_token")
    val accessToken: String = "",
    @field:Json(name = "token_type")
    val tokenType: String = " ",
    @field:Json(name = "refresh_token")
    val refreshToken: String = "",
    @field:Json(name = "error_description")
    val errorDescription: String = "",
    @field:Json(name = "error_uri")
    val errorUri: String = ""
)