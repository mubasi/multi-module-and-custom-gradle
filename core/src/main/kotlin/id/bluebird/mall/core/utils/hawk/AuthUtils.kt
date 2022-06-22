package id.bluebird.mall.core.utils.hawk

import com.orhanobut.hawk.Hawk

object AuthUtils {
    private const val ACCESS_TOKEN = "accessToken"

    fun String.putAccessToken(): Boolean = Hawk.put(ACCESS_TOKEN, this)

    fun getAccessToken(): String = Hawk.get(ACCESS_TOKEN) ?: ""

    fun logout() {
        Hawk.deleteAll()
    }
}