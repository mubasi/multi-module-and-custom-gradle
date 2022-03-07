package id.bluebird.mall.officer

import android.app.Application
import id.bluebird.mall.officer.common.di.initDependencyInjection

class OfficerApp: Application() {
    override fun onCreate() {
        super.onCreate()
        initDependencyInjection(this)
    }
}