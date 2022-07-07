package id.bluebird.mall.officer

import android.app.Application
import com.orhanobut.hawk.Hawk
import id.bluebird.mall.core.utils.OkHttpChannel
import id.bluebird.mall.officer.AppModule.initDependencyInjection

class OfficerApp : Application() {
    override fun onCreate() {
        super.onCreate()
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