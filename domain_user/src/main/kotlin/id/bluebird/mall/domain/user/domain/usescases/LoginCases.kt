package id.bluebird.mall.domain.user.domain.usescases

import id.bluebird.mall.core.utils.hawk.AuthUtils.putAccessToken
import id.bluebird.mall.core.utils.hawk.UserUtils
import id.bluebird.mall.domain.user.UserRepository
import id.bluebird.mall.domain.user.domain.intercator.Login
import id.bluebird.mall.domain.user.model.LoginParam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transform

class LoginCaseImpl(private val userRepository: UserRepository) : Login {
    companion object {
        const val USERNAME_EMPTY = "usernameIsEmpty"
        const val PASSWORD_EMPTY = "passwordIsEmpty"
    }

    override fun invoke(username: String?, password: String?): Flow<Boolean> =
        flow {
            val param = LoginParam(username = username ?: "", password = password ?: "")
            if (username.isNullOrEmpty()) {
                throw NullPointerException(USERNAME_EMPTY)
            }
            if (password.isNullOrEmpty()) {
                throw NullPointerException(PASSWORD_EMPTY)
            }
            emitAll(userRepository.doLogin(loginParam = param).transform {
                val result = it.accessToken.putAccessToken()
                UserUtils.putUser(
                    userId = it.userId,
                    locationId = it.locationId,
                    uuid = it.uuid,
                    userRole = it.userRole,
                    username = username,
                    fleetTypeId = it.fleetType,
                    isUserAirport = it.isAirport
                )
                emit(result)
            })
        }
}