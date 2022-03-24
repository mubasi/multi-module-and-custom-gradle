package id.bluebird.mall.officer.utils

import com.orhanobut.hawk.Hawk
import kotlin.random.Random

class AuthUtils {

    companion object {
        private const val APP_IDENTIFIER = "AppIdentifier"
        private const val ACCESS_TOKEN = "accessToken"

        fun putAccessToken(accessToken: String) {
            Hawk.put(ACCESS_TOKEN, accessToken)
        }

        fun getAccessToken(): String = Hawk.get(ACCESS_TOKEN) ?: ""

        fun logout() {
            Hawk.deleteAll()
        }

        fun getAppIdentifier() = Hawk.get(APP_IDENTIFIER) ?: ""

        fun generateAppIdentifier(): String {
            val newId = "${Random(100000).nextInt()}-D"
            if (getAppIdentifier().isEmpty()) {
                Hawk.put(APP_IDENTIFIER, newId)
            }
            return newId
        }
    }
}