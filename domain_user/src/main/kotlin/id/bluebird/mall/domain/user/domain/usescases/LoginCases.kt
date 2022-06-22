package id.bluebird.mall.domain.user.domain.usescases

import id.bluebird.mall.domain.user.domain.intercator.Login
import id.bluebird.mall.domain.user.model.LoginParam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

class LoginCaseImpl() : Login {
    companion object {
        const val USERNAME_EMPTY = "usernameIsEmpty"
        const val PASSWORD_EMPTY = "passwordIsEmpty"
    }

    private lateinit var mCollector: FlowCollector<Long>

    override fun invoke(username: String?, password: String?): Flow<Long> =
        flow {
            mCollector = this
            val param = LoginParam(username = username ?: "", password = password ?: "")
            if (username.isNullOrEmpty()) {
                throw NullPointerException(USERNAME_EMPTY)
            }
            if (password.isNullOrEmpty()) {
                throw NullPointerException(PASSWORD_EMPTY)
            }
            resultFromServer(param)
        }

    private suspend fun resultFromServer(param: LoginParam) {
    }
}