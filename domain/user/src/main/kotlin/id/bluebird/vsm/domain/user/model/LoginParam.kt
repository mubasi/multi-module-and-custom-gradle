package id.bluebird.vsm.domain.user.model

import androidx.annotation.Keep


@Keep
data class LoginParam(
    val username: String,
    val password: String,
    val fleetType: Long = DEFAULT_FLEET_TYPE
) {
    internal companion object {
        const val DEFAULT_FLEET_TYPE = 1L
    }
}
