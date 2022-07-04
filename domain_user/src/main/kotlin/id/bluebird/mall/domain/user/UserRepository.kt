package id.bluebird.mall.domain.user

import id.bluebird.mall.domain.user.model.LoginParam
import kotlinx.coroutines.flow.Flow
import proto.UserOuterClass

interface UserRepository {
    fun doLogin(loginParam: LoginParam): Flow<UserOuterClass.UserLoginResponse>
    fun forceLogout(uuid: String): Flow<UserOuterClass.ForceLogoutResponse>
    fun deleteUser(uuid: String, by: Long): Flow<UserOuterClass.DeleteUserResponse>
    fun searchUser(param: String): Flow<UserOuterClass.SearchUserResponse>
}