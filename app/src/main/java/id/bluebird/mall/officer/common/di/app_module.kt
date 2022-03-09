package id.bluebird.mall.officer.common.di

import android.content.Context
import id.bluebird.mall.officer.ui.login.LoginViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.dsl.module

private val vmModule = module {
    viewModel { LoginViewModel() }
}

private val casesUser = module {

}

private val modules = listOf(vmModule, casesUser)

lateinit var koin: Koin

fun initDependencyInjection(context: Context) {
    koin = startKoin {
        androidContext(context)
        modules(modules)
    }.koin
}
