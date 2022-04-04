package id.bluebird.mall.officer.common.uses_case.user

import id.bluebird.mall.officer.common.CasesResult
import id.bluebird.mall.officer.common.network.model.LoginParam
import id.bluebird.mall.officer.common.repository.UserRepository
import id.bluebird.mall.officer.utils.ExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

interface LoginCase {
    operator fun invoke(username: String?, password: String?): Flow<CasesResult<String>>
}

class LoginCaseImpl(
    private val userRepository: UserRepository
) : LoginCase {
    companion object {
        const val USERNAME_EMPTY = "usernameIsEmpty"
        const val PASSWORD_EMPTY = "passwordIsEmpty"
    }

    private lateinit var mCollector: FlowCollector<CasesResult<String>>

    override fun invoke(username: String?, password: String?): Flow<CasesResult<String>> =
        flow {
            mCollector = this
            val param = LoginParam(username ?: "", password ?: "")
            if (username.isNullOrEmpty()) {
                throw NullPointerException(USERNAME_EMPTY)
            }
            if (password.isNullOrEmpty()) {
                throw NullPointerException(PASSWORD_EMPTY)
            }
            resultFromServer(param)
        }

    private suspend fun resultFromServer(param: LoginParam) {
        val result = userRepository.userLogin(param)
        if (result.isSuccessful) {
            result.body()?.let {
                mCollector.emit(CasesResult.OnSuccess(it.accessToken))
            } ?: run {
                throw NullPointerException(ExceptionHandler.RESPONSE_BODY_IS_NULL)
            }
        } else {
            mCollector.emit(
                CasesResult.OnError(
                    ExceptionHandler.generateExceptionCode(
                        result.code(),
                        result.errorBody()
                    )
                )
            )
        }
    }
}