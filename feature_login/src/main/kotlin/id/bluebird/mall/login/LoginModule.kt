package id.bluebird.mall.login

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object LoginModule {
    val loginModule = module {
        viewModel { LoginViewModel(get()) }
    }
}