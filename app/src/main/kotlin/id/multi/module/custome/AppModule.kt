package id.multi.module.custome

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.Koin
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

object AppModule {

    private val vmModule = module {
//        viewModel { SplashViewModel() }
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