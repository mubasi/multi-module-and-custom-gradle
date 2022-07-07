package id.bluebird.mall.officer

import android.content.Context
import id.bluebird.mall.domain.user.UserRepository
import id.bluebird.mall.domain.user.UserRepositoryImpl
import id.bluebird.mall.domain.user.domain.intercator.*
import id.bluebird.mall.domain.user.domain.usescases.*
import id.bluebird.mall.domain_location.LocationRepository
import id.bluebird.mall.domain_location.LocationRepositoryImpl
import id.bluebird.mall.domain_location.domain.cases.GetSubLocationByLocationIdCases
import id.bluebird.mall.domain_location.domain.interactor.GetSubLocationByLocationId
import id.bluebird.mall.feature_user_management.create.CreateUserViewModel
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
        viewModel { CreateUserViewModel(get(), get(), get(), get()) }
    }

    private val userCases = module {
        single<DeleteUser> { DeleteUserCases(get()) }
        single<ForceLogout> { LogoutCasesImpl(get()) }
        single<SearchUser> { SearchUserCases(get()) }
        single<Login> { LoginCaseImpl(get()) }
        single<CreateEditUser> { CreateEditUserCases(get()) }
        single<GetRoles> { GetRolesCases(get()) }
        single<GetUserById> { GetUserByIdCases(get()) }
    }

    private val locationCases = module {
        single<GetSubLocationByLocationId> { GetSubLocationByLocationIdCases(get()) }
    }

    private val repository = module {
        single<UserRepository> { UserRepositoryImpl() }
        single<LocationRepository> { LocationRepositoryImpl() }
    }

    lateinit var koin: Koin

    fun initDependencyInjection(context: Context) {
        koin = startKoin {
            androidContext(context)
            modules(
                listOf(
                    locationCases,
                    userCases,
                    repository,
                    vmModule,
                )
            )
        }.koin
    }
}