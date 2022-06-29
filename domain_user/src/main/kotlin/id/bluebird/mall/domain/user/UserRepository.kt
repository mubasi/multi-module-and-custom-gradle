package id.bluebird.mall.domain.user

import id.bluebird.mall.domain.user.model.LoginParam
import kotlinx.coroutines.flow.Flow
import proto.UserOuterClass

interface UserRepository {
    fun doLogin(loginParam: LoginParam): Flow<UserOuterClass.UserLoginResponse>
    fun doLogout(id: Long)
}