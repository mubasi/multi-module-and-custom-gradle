package id.bluebird.vsm.pangkalan

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.core.utils.OkHttpChannel
import id.bluebird.vsm.pangkalan.AppModule.initDependencyInjection

class OfficerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseAnalytics.getInstance(this)
        initDependencyInjection(this)
        OkHttpChannel.initChannel(tokenExpiredCallback = tokenExpiredCallback)
        Hawk.init(this).build()
    }

    private val tokenExpiredCallback = object : OkHttpChannel.Companion.TokenExpiredCallback {
        override fun onTokenExpired() {
//            logout(true, this@VsmApp)
        }
    }
}