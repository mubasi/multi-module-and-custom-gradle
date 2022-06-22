package id.bluebird.mall.home

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object HomeModule {
    val homeModule = module {
        viewModel { HomeViewModel(get(), get(), get(), get()) }
    }
}