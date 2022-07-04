package id.bluebird.mall.officer

import android.content.Context
import id.bluebird.mall.domain.user.UserRepository
import id.bluebird.mall.domain.user.UserRepositoryImpl
import id.bluebird.mall.domain.user.domain.intercator.DeleteUser
import id.bluebird.mall.domain.user.domain.intercator.ForceLogout
import id.bluebird.mall.domain.user.domain.intercator.Login
import id.bluebird.mall.domain.user.domain.intercator.SearchUser
import id.bluebird.mall.domain.user.domain.usescases.DeleteUserCases
import id.bluebird.mall.domain.user.domain.usescases.LoginCaseImpl
import id.bluebird.mall.domain.user.domain.usescases.LogoutCasesImpl
import id.bluebird.mall.domain.user.domain.usescases.SearchUserCases
import id.bluebird.mall.feature_user_management.list.UserManagementViewModel
import id.bluebird.mall.home.HomeViewModel
import id.bluebird.mall.login.LoginViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.Koin
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

object AppModule {
    private val vmModule = module {
        viewModel { LoginViewModel(get()) }
        viewModel { HomeViewModel(get()) }
        viewModel { UserManagementViewModel(get(), get(), get()) }
    }

    private val userCases = module {
        single<DeleteUser> { DeleteUserCases(get()) }
        single<ForceLogout> { LogoutCasesImpl(get()) }
        single<SearchUser> { SearchUserCases(get()) }
        single<Login> { LoginCaseImpl(get()) }
    }

    private val repository = module {
        single<UserRepository> { UserRepositoryImpl() }
    }

    lateinit var koin: Koin

    fun initDependencyInjection(context: Context) {
        koin = startKoin {
            androidContext(context)
            modules(
                listOf(
                    userCases,
                    repository,
                    vmModule,
                )
            )
        }.koin
    }
}