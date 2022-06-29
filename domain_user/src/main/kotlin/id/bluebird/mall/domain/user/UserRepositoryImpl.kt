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
        val request = UserOuterClass.UserLoginRequest.newBuilder()
            .setUsername(loginParam.username)
            .setPassword(loginParam.password)
            .setFleetType(loginParam.fleetType)
            .build()
        val response = userGrpc.userLogin(request)
        emit(response)
    }

    override fun doLogout(id: Long) {
        TODO("Not yet implemented")
    }
}