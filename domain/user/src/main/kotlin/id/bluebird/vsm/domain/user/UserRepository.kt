package id.bluebird.vsm.domain.user

import id.bluebird.vsm.domain.user.model.CreateUserParam
import id.bluebird.vsm.domain.user.model.LoginParam
import kotlinx.coroutines.flow.Flow
import proto.UserOuterClass

interface UserRepository {
    fun doLogin(loginParam: LoginParam): Flow<UserOuterClass.UserLoginResponse>
    fun forceLogout(uuid: String): Flow<UserOuterClass.ForceLogoutResponse>
    fun deleteUser(uuid: String, by: Long): Flow<UserOuterClass.DeleteUserResponse>
    fun searchUser(param: String?): Flow<UserOuterClass.SearchUserResponse>
    fun getRoles(): Flow<UserOuterClass.GetRolesResponse>
    fun createUser(model: CreateUserParam): Flow<UserOuterClass.CreateUserResponse>
    fun editUser(model: CreateUserParam): Flow<UserOuterClass.EditUserResponse>
    fun getUserById(id: Long): Flow<UserOuterClass.GetUserByIdResponse>
    fun getUserLocationAssign(
        subLocationsId: List<Long>,
        locationId: Long
    ): List<UserOuterClass.userAssignmentItem>
    fun getSplashConfig(key: String): Flow<UserOuterClass.SplashConfigResponse>
    fun getUserAssignment(id: Long): Flow<UserOuterClass.UserAssignmentResponse>
}