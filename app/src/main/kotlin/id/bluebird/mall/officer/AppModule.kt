package id.bluebird.mall.officer

import android.content.Context
import id.bluebird.mall.domain.user.UserRepository
import id.bluebird.mall.domain.user.UserRepositoryImpl
import id.bluebird.mall.domain.user.domain.intercator.*
import id.bluebird.mall.domain.user.domain.usescases.*
import id.bluebird.mall.domain_fleet.FleetRepository
import id.bluebird.mall.domain_fleet.FleetRepositoryImpl
import id.bluebird.mall.domain_fleet.domain.cases.*
import id.bluebird.mall.domain_fleet.domain.interactor.*
import id.bluebird.mall.domain_location.LocationRepository
import id.bluebird.mall.domain_location.LocationRepositoryImpl
import id.bluebird.mall.domain_location.domain.cases.GetSubLocationByLocationIdCases
import id.bluebird.mall.domain_location.domain.interactor.GetSubLocationByLocationId
import id.bluebird.mall.feature_queue_fleet.add_fleet.AddFleetViewModel
import id.bluebird.mall.feature_queue_fleet.main.QueueFleetViewModel
import id.bluebird.mall.feature_queue_fleet.request_fleet.RequestFleetDialogViewModel
import id.bluebird.mall.feature_user_management.create.CreateUserViewModel
import id.bluebird.mall.feature_user_management.list.UserManagementViewModel
import id.bluebird.mall.home.HomeViewModel
import id.bluebird.mall.login.LoginViewModel
import id.bluebird.mall.officer.logout.LogoutDialogViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.Koin
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

object AppModule {
    private val vmModule = module {
        viewModel { LoginViewModel(get()) }
        viewModel { HomeViewModel(get()) }
        viewModel { QueueFleetViewModel(get(), get(), get()) }
        viewModel { UserManagementViewModel(get(), get(), get()) }
        viewModel { CreateUserViewModel(get(), get(), get(), get()) }
        viewModel { RequestFleetDialogViewModel(get()) }
        viewModel { LogoutDialogViewModel(get()) }
        viewModel { AddFleetViewModel(get(), get()) }
    }

    private val userCases = module {
        single<DeleteUser> { DeleteUserCases(get()) }
        single<ForceLogout> { LogoutCasesImpl(get()) }
        single<SearchUser> { SearchUserCases(get()) }
        single<Login> { LoginCaseImpl(get()) }
        single<CreateEditUser> { CreateEditUserCases(get()) }
        single<GetRoles> { GetRolesCases(get()) }
        single<GetUserId> { GetUserIdCases(get()) }
    }

    private val fleetCases = module {
        single<GetCount> { GetCountCases(get()) }
        single<RequestFleet> { RequestFleetUseCases(get()) }
        single<SearchFleet> { SearchFleetUseCases(get()) }
        single<AddFleet> { AddFleetUseCases(get()) }
        single<GetListFleet> { GetListFleetUseCases(get()) }
    }

    private val locationCases = module {
        single<GetSubLocationByLocationId> { GetSubLocationByLocationIdCases(get()) }
    }

    private val repository = module {
        single<UserRepository> { UserRepositoryImpl() }
        single<LocationRepository> { LocationRepositoryImpl() }
        single<FleetRepository> { FleetRepositoryImpl() }
    }

    lateinit var koin: Koin

    fun initDependencyInjection(context: Context) {
        koin = startKoin {
            androidContext(context)
            modules(
                listOf(
                    locationCases,
                    fleetCases,
                    userCases,
                    repository,
                    vmModule,
                )
            )
        }.koin
    }
}