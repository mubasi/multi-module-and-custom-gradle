package id.multi.module.custome

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.orhanobut.hawk.Hawk
import id.multi.module.custome.core.extensions.getVersionCode
import id.multi.module.custome.core.utils.OkHttpChannel
import id.multi.module.custome.core.utils.hawk.AuthUtils
import id.multi.module.custome.core.utils.hawk.UserUtils
import id.multi.module.custome.AppModule.initDependencyInjection

class OfficerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Hawk.init(this).build()
//        FirebaseAnalytics.getInstance(this)
//        FirebaseCrashlytics.getInstance().setUserId(UserUtils.getUserId().toString())
//        FirebaseCrashlytics.getInstance().setCustomKey("role", UserUtils.getPrivilege() ?: "-")
        initDependencyInjection(this)
        OkHttpChannel.initChannel(tokenExpiredCallback = tokenExpiredCallback, applicationContext.getVersionCode())
    }

    private val tokenExpiredCallback = object : OkHttpChannel.Companion.TokenExpiredCallback {
        override fun onTokenExpired() {
            resetActivity()
        }
    }

    private fun resetActivity() {
//        LocationNavigationTemporary.removeTempData()
        FirebaseAuth.getInstance().signOut()
        AuthUtils.logout()
        startActivity(MainActivity.startNewIntent(this@OfficerApp))
    }
}