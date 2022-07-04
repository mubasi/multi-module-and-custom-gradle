package id.bluebird.mall.domain.user

import id.bluebird.mall.core.utils.OkHttpChannel
import id.bluebird.mall.domain.user.model.LoginParam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import proto.UserGrpc
import proto.UserOuterClass

class UserRepositoryImpl(
    private val userGrpc: UserGrpc.UserBlockingStub = UserGrpc.newBlockingStub(
        OkHttpChannel.channel
    )
) : UserRepository {
    override fun doLogin(loginParam: LoginParam): Flow<UserOuterClass.UserLoginResponse> = flow {
        val request = UserOuterClass.UserLoginRequest.newBuilder().apply {
            username = loginParam.username
            password = loginParam.password
            fleetType = loginParam.fleetType
        }
            .build()
        val response = userGrpc.userLogin(request)
        emit(response)
    }

    override fun forceLogout(uuid: String): Flow<UserOuterClass.ForceLogoutResponse> = flow {
        val request = UserOuterClass.ForceLogoutRequest.newBuilder().apply {
            this.uuid = uuid
        }.build()
        val response = userGrpc.forceLogout(request)
        emit(response)
    }

    override fun deleteUser(uuid: String, by: Long): Flow<UserOuterClass.DeleteUserResponse> =
        flow {
            val request = UserOuterClass.DeleteUserRequest.newBuilder()
                .apply {
                    this.uuid = uuid
                    deletedBy = by
                }.build()
            val response = userGrpc.deleteUser(request)
            emit(response)
        }

    override fun searchUser(param: String): Flow<UserOuterClass.SearchUserResponse> = flow {
        val request = UserOuterClass.SearchUserRequest.newBuilder()
            .setUsername(param)
            .build()
        val response = userGrpc.searchUser(request)
        emit(response)
    }
}