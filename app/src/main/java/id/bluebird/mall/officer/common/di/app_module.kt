package id.bluebird.mall.officer.common.di

import android.content.Context
import id.bluebird.mall.officer.case.queue.SkipQueueCases
import id.bluebird.mall.officer.case.queue.SkipQueueCasesImpl
import id.bluebird.mall.officer.common.Mqtt
import id.bluebird.mall.officer.ui.home.HomeViewModel
import id.bluebird.mall.officer.ui.login.LoginViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.dsl.module

private val vmModule = module {
    viewModel { LoginViewModel() }
    viewModel { HomeViewModel(get()) }
}

private val queueCases = module {
    single<SkipQueueCases> { SkipQueueCasesImpl() }
}

private val connectionModule = module {
    single { Mqtt(androidContext()) }
}

private val modules = listOf(vmModule, queueCases, connectionModule)

lateinit var koin: Koin

fun initDependencyInjection(context: Context) {
    koin = startKoin {
        androidContext(context)
        modules(modules)
    }.koin
}
