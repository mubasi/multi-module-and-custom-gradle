package id.bluebird.mall.officer

import android.content.Context
import id.bluebird.mall.domain.user.domain.intercator.Login
import id.bluebird.mall.domain.user.domain.usescases.LoginCaseImpl
import id.bluebird.mall.home.HomeViewModel
import id.bluebird.mall.login.LoginViewModel
import id.bluebird.mall.queue.*
import id.bluebird.mall.user.domain.LogoutCases
import id.bluebird.mall.user.domain.LogoutCasesImpl
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.Koin
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

object AppModule {
    private val vmModule = module {
        viewModel { LoginViewModel(get()) }
        viewModel { HomeViewModel(get(), get(), get(), get()) }
    }

    private val userCases = module {
        single<LogoutCases> { LogoutCasesImpl() }
        single<Login> { LoginCaseImpl() }
    }
    private val queueCases = module {
        single<SkipQueueCases> { SkipQueueCasesImpl() }
        single<RestoreQueueCases> { RestoreQueueCasesImpl() }
        single<RitaseCase> { RitaseCaseImpl() }
    }


    lateinit var koin: Koin

    fun initDependencyInjection(context: Context) {
        koin = startKoin {
            androidContext(context)
            modules(
                listOf(
                    vmModule,
                    userCases,
                    queueCases
                )
            )
        }.koin
    }
}