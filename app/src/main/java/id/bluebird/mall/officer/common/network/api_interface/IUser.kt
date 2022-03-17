package id.bluebird.mall.officer.common.network.api_interface

import id.bluebird.mall.officer.common.network.model.LoginParam
import id.bluebird.mall.officer.common.network.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface IUser {
    @POST("officer/")
    suspend fun login(@Body loginParam: LoginParam): Response<LoginResponse>
}