package id.bluebird.vsm.core.utils.hawk

import com.orhanobut.hawk.Hawk

object AuthUtils {
    private const val ACCESS_TOKEN = "accessToken"
    private const val APP_UUID = "app_uuid"

    fun getAppUUID(): String {
        var temp: String? = Hawk.get(APP_UUID)
        return if (temp == null) {
            temp = java.util.UUID.randomUUID().toString().substring(0, 31)
            Hawk.put(APP_UUID, temp)
            return temp
        } else {
            temp
        }
    }

    fun String.putAccessToken(): Boolean = Hawk.put(ACCESS_TOKEN, this)

    fun getAccessToken(): String = Hawk.get(ACCESS_TOKEN) ?: ""

    fun logout() {
        Hawk.deleteAll()
    }
}