package id.bluebird.mall.officer.common.di

import android.content.Context
import id.bluebird.mall.officer.common.Mqtt
import id.bluebird.mall.officer.common.network.Retrofit
import id.bluebird.mall.officer.common.network.api_interface.IUser
import id.bluebird.mall.officer.common.repository.UserRepository
import id.bluebird.mall.officer.common.repository.UserRepositoryImpl
import id.bluebird.mall.officer.common.uses_case.queue.RestoreQueueCases
import id.bluebird.mall.officer.common.uses_case.queue.RestoreQueueCasesImpl
import id.bluebird.mall.officer.common.uses_case.queue.SkipQueueCases
import id.bluebird.mall.officer.common.uses_case.queue.SkipQueueCasesImpl
import id.bluebird.mall.officer.common.uses_case.user.LoginCase
import id.bluebird.mall.officer.common.uses_case.user.LoginCaseImpl
import id.bluebird.mall.officer.common.uses_case.user.LogoutCases
import id.bluebird.mall.officer.common.uses_case.user.LogoutCasesImpl
import id.bluebird.mall.officer.ui.home.HomeViewModel
import id.bluebird.mall.officer.ui.login.LoginViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.dsl.module

private val vmModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
}

private val repositoryModule = module {
    single<UserRepository> { UserRepositoryImpl(get()) }
}

private val userCases = module {
    single<LogoutCases> { LogoutCasesImpl() }
    single<LoginCase> { LoginCaseImpl(get()) }
}
private val queueCases = module {
    single<SkipQueueCases> { SkipQueueCasesImpl() }
    single<RestoreQueueCases> { RestoreQueueCasesImpl() }
}
private val retrofitModules = module {
    val retrofit = Retrofit.getRetrofit()
    factory { retrofit.create(IUser::class.java) }
}

private val connectionModule = module {
    single { Mqtt(androidContext()) }
}

private val modules =
    listOf(vmModule, repositoryModule, queueCases, retrofitModules, userCases, connectionModule)

lateinit var koin: Koin

fun initDependencyInjection(context: Context) {
    koin = startKoin {
        androidContext(context)
        modules(modules)
    }.koin
}
