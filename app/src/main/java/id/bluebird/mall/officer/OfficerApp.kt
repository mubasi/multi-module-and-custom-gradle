package id.bluebird.mall.officer

import android.app.Application
import com.orhanobut.hawk.Hawk
import id.bluebird.mall.officer.common.di.initDependencyInjection
import id.bluebird.mall.officer.utils.AuthUtils

class OfficerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initDependencyInjection(this)
        Hawk.init(this).build()
        AuthUtils.generateAppIdentifier()
    }
}