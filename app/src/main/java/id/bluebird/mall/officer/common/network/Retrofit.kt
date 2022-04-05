package id.bluebird.mall.officer.common.network

import android.util.Log
import id.bluebird.mall.officer.utils.AuthUtils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object Retrofit {
    fun getRetrofit() = mRetrofit

    const val CONTENT_TYPE_CHARSET = "application/json; charset=UTF-8"

    private const val AUTHORIZATION = "Authorization"
    private const val RESPONSE = "Response Api"
    private val mRetrofit = Retrofit.Builder().baseUrl("http://192.168.1.11:9090/")
        .client(okHttpClient())
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private fun okHttpClient() = OkHttpClient.Builder().addInterceptor(interceptor()).build()

    private fun interceptor(): Interceptor {
        return Interceptor {
            val request = it.request()
            val requestBuilder = request.newBuilder()
            val token = AuthUtils.getAccessToken()
            requestBuilder.addHeader("Content-Type", CONTENT_TYPE_CHARSET)
            if (token.isNotEmpty()) {
                requestBuilder.addHeader(AUTHORIZATION, "bearer $token")
            }
            val response = it.proceed(requestBuilder.build())
            Log.d(RESPONSE, "${response.code} ${request.method} ${request.url} ${request.body}")
            response
        }
    }
}