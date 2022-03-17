package id.bluebird.mall.officer.common.repository

import id.bluebird.mall.officer.common.network.api_interface.IUser
import id.bluebird.mall.officer.common.network.model.LoginParam
import id.bluebird.mall.officer.common.network.model.LoginResponse
import retrofit2.Response

interface UserRepository {
    suspend fun userLogin(param: LoginParam): Response<LoginResponse>
}

class UserRepositoryImpl(private val mIUser: IUser) : UserRepository {
    override suspend fun userLogin(param: LoginParam): Response<LoginResponse> = mIUser.login(param)

}