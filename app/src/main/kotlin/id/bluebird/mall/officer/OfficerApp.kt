package id.bluebird.mall.officer

import android.app.Application
import com.orhanobut.hawk.Hawk
import id.bluebird.mall.officer.AppModule.initDependencyInjection

class OfficerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initDependencyInjection(this)
        Hawk.init(this).build()
    }
}