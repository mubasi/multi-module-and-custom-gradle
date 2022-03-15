package id.bluebird.mall.officer.utils

import com.orhanobut.hawk.Hawk

class AuthUtils {

    companion object {
        private const val ACCESS_TOKEN = "accessToken"

        fun putAccessToken(accessToken: String) {
            Hawk.put(ACCESS_TOKEN, accessToken)
        }

        fun getAccessToken(): String = Hawk.get(ACCESS_TOKEN) ?: ""

        fun logout() {
            Hawk.deleteAll()
        }
    }
}