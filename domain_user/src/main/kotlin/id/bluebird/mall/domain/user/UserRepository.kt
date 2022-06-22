package id.bluebird.mall.domain.user

import id.bluebird.mall.domain.user.model.LoginParam
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun doLogin(loginParam: LoginParam): Flow<String>
    fun doLogout(id: Long)
}