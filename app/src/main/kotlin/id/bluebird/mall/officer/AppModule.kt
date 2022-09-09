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
import id.bluebird.mall.domain_location.domain.cases.GetLocationsCases
import id.bluebird.mall.domain_location.domain.cases.GetLocationsWithSubUseCases
import id.bluebird.mall.domain_location.domain.cases.GetSubLocationByLocationIdCases
import id.bluebird.mall.domain_location.domain.cases.UpdateBufferCases
import id.bluebird.mall.domain_location.domain.interactor.GetLocations
import id.bluebird.mall.domain_location.domain.interactor.GetLocationsWithSub
import id.bluebird.mall.domain_location.domain.interactor.GetSubLocationByLocationId
import id.bluebird.mall.domain_location.domain.interactor.UpdateBuffer
import id.bluebird.mall.domain_pasenger.QueueReceiptRepository
import id.bluebird.mall.domain_pasenger.QueueReceiptRepositoryimpl
import id.bluebird.mall.domain_pasenger.domain.cases.*
import id.bluebird.mall.domain_pasenger.domain.interactor.*
import id.bluebird.mall.feature.select_location.SelectLocationViewModel
import id.bluebird.mall.feature_monitoring.edit_buffer.EditBufferViewModel
import id.bluebird.mall.feature_monitoring.main.MonitoringViewModel
import id.bluebird.mall.feature_queue_fleet.adapter.FleetsAdapter
import id.bluebird.mall.feature_queue_fleet.add_fleet.AddFleetViewModel
import id.bluebird.mall.feature_queue_fleet.depart_fleet.DepartFleetViewModel
import id.bluebird.mall.feature_queue_fleet.main.QueueFleetViewModel
import id.bluebird.mall.feature_queue_fleet.request_fleet.RequestFleetDialogViewModel
import id.bluebird.mall.feature_queue_fleet.search_fleet.SearchFleetViewModel
import id.bluebird.mall.feature_user_management.create.CreateUserViewModel
import id.bluebird.mall.feature_user_management.list.UserManagementViewModel
import id.bluebird.mall.feature_user_management.search_location.SearchLocationViewModel
import id.bluebird.mall.home.dialog_delete_skipped.DialogDeleteSkippedViewModel
import id.bluebird.mall.home.dialog_queue_receipt.DialogQueueReceiptViewModel
import id.bluebird.mall.home.dialog_restore_skipped.DialogRestoreSkippedViewModel
import id.bluebird.mall.home.dialog_skip_queue.DialogSkipQueueViewModel
import id.bluebird.mall.home.main.QueuePassengerViewModel
import id.bluebird.mall.home.queue_search.QueueSearchViewModel
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
        viewModel { UserManagementViewModel(get()) }
        viewModel { CreateUserViewModel(get(), get(), get(), get(), get(), get()) }
        viewModel { RequestFleetDialogViewModel(get()) }
        viewModel { LogoutDialogViewModel(get()) }
        viewModel { AddFleetViewModel(get(), get(), get()) }
        viewModel { SearchFleetViewModel() }
        viewModel { DialogQueueReceiptViewModel(get(), get(), get()) }
        viewModel { QueueTicketViewModel(get()) }
        viewModel { QueuePassengerViewModel(get(), get(), get(), get(), get()) }
        viewModel { DialogSkipQueueViewModel(get()) }
        viewModel { DepartFleetViewModel() }
        viewModel { DialogDeleteSkippedViewModel(get()) }
        viewModel { DialogRestoreSkippedViewModel(get()) }
        viewModel { MonitoringViewModel(get()) }
        viewModel { EditBufferViewModel(get()) }
        viewModel { SearchLocationViewModel(get()) }
        viewModel { QueueSearchViewModel(get()) }
        viewModel { SelectLocationViewModel(get()) }
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
        single<DepartFleet> { DepartFleetUseCases(get()) }
        single<Monitoring> { MonitoringUseCases() }
    }

    private val locationCases = module {
        single<GetSubLocationByLocationId> { GetSubLocationByLocationIdCases(get()) }
        single<UpdateBuffer> { UpdateBufferCases(get()) }
        single<GetLocations> { GetLocationsCases(get()) }
        single<GetLocationsWithSub> { GetLocationsWithSubUseCases(get()) }
    }

    private val passengerCases = module {
        single<GetQueueReceipt> { GetQueueReceiptCases(get()) }
        single<TakeQueue> { TakeQueueCases(get()) }
        single<CurrentQueue> { CurrentQueueCases(get()) }
        single<SkipQueue> { SkipQueueCases(get()) }
        single<ListQueueSkipped> { ListQueueSkippedCases(get()) }
        single<ListQueueWaiting> { ListQueueWaitingCases(get()) }
        single<GetWaitingQueue> { GetWaitingQueueCases(get()) }
        single<GetCurrentQueue> { GetCurrentQueueCase(get()) }
        single<SearchWaitingQueue> { SearchWaitingQueueCases(get()) }
        single<DeleteSkipped> { DeleteSkippedCases(get()) }
        single<RestoreSkipped> { RestoreSkippedCases(get()) }
        single<CounterBar> { CounterBarCases(get()) }
        single<SearchQueue> { SearchQueueCases(get()) }
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