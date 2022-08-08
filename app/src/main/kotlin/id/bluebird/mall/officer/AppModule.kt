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
import id.bluebird.mall.domain_pasenger.QueueReceiptRepository
import id.bluebird.mall.domain_pasenger.QueueReceiptRepositoryimpl
import id.bluebird.mall.domain_pasenger.domain.cases.*
import id.bluebird.mall.domain_pasenger.domain.interactor.*
import id.bluebird.mall.feature_queue_fleet.adapter.FleetsAdapter
import id.bluebird.mall.feature_queue_fleet.add_fleet.AddFleetViewModel
import id.bluebird.mall.feature_queue_fleet.depart_fleet.DepartFleetViewModel
import id.bluebird.mall.feature_queue_fleet.main.QueueFleetViewModel
import id.bluebird.mall.feature_queue_fleet.request_fleet.RequestFleetDialogViewModel
import id.bluebird.mall.feature_queue_fleet.search_fleet.SearchFleetViewModel
import id.bluebird.mall.feature_user_management.create.CreateUserViewModel
import id.bluebird.mall.feature_user_management.list.UserManagementViewModel
import id.bluebird.mall.home.dialog_queue_receipt.DialogQueueReceiptViewModel
import id.bluebird.mall.home.dialog_skip_queue.DialogSkipQueueViewModel
import id.bluebird.mall.home.main.QueuePassengerViewModel
import id.bluebird.mall.home.queue_ticket.QueueTicketViewModel
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
        viewModel { QueueFleetViewModel(get(), get(), get(), get(), get()) }
        viewModel { QueueFleetViewModel(get(), get(), get(), get(), get()) }
        viewModel { UserManagementViewModel(get(), get(), get()) }
        viewModel { CreateUserViewModel(get(), get(), get(), get()) }
        viewModel { RequestFleetDialogViewModel(get()) }
        viewModel { LogoutDialogViewModel(get()) }
        viewModel { AddFleetViewModel(get(), get(), get()) }
        viewModel { SearchFleetViewModel() }
        viewModel { DialogQueueReceiptViewModel(get(), get(), get()) }
        viewModel { QueueTicketViewModel(get()) }
        viewModel { QueuePassengerViewModel(get(), get(), get(), get()) }
        viewModel { DialogSkipQueueViewModel(get()) }
        viewModel { DepartFleetViewModel() }
        viewModel { DialogQueueReceiptViewModel(get(), get(), get()) }
        viewModel { QueueTicketViewModel(get()) }
        viewModel { QueuePassengerViewModel(get(), get(), get(), get()) }
        viewModel { DialogSkipQueueViewModel(get()) }
        viewModel { DepartFleetViewModel() }
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

    private val passengerCases = module {
        single<GetQueueReceipt> { GetQueueReceiptCases(get()) }
        single<TakeQueue> { TakeQueueCases(get()) }
        single<CurrentQueue> { CurrentQueueCases(get()) }
        single<SkipQueue> { SkipQueueCases(get()) }
        single<ListQueueSkipped> { ListQueueSkippedCases(get()) }
        single<ListQueueWaiting> { ListQueueWaitingCases(get()) }
        single<ListQueueSkipped> { ListQueueSkippedCases(get()) }
        single<GetWaitingQueue> {GetWaitingQueueCases(get())}
        single<GetCurrentQueue> {GetCurrentQueueCase(get())}
        single<SearchWaitingQueue> {SearchWaitingQueueCases(get())}
    }

    private val repository = module {
        single<UserRepository> { UserRepositoryImpl() }
        single<LocationRepository> { LocationRepositoryImpl() }
        single<FleetRepository> { FleetRepositoryImpl() }
        single<QueueReceiptRepository> { QueueReceiptRepositoryimpl() }
    }

    private val adapter = module {
        single { FleetsAdapter() }
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
                    adapter,
                    passengerCases
                )
            )
        }.koin
    }
}