package id.bluebird.vsm.pangkalan

import android.content.Context
import id.bluebird.vsm.feature.splash.SplashViewModel
import id.bluebird.vsm.pangkalan.logout.LogoutDialogViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.Koin
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

object AppModule {

    private val vmModule = module {
        viewModel { SplashViewModel() }
        viewModel { LogoutDialogViewModel() }
    }
    lateinit var koin: Koin

    fun initDependencyInjection(context: Context) {
        koin = startKoin {
            androidContext(context)
            modules(
                listOf(
                    vmModule,
                )
            )
        }.koin
    }
}