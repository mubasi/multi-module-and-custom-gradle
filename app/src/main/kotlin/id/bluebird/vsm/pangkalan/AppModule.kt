package id.bluebird.vsm.pangkalan

import android.content.Context
import id.bluebird.vsm.domain.airport_assignment.AirportAssignmentRepository
import id.bluebird.vsm.domain.airport_assignment.AirportAssignmentRepositoryImpl
import id.bluebird.vsm.domain.airport_assignment.domain.cases.*
import id.bluebird.vsm.domain.airport_assignment.domain.interactor.*
import id.bluebird.vsm.domain.airport_location.AirportLocationRepositoryImpl
import id.bluebird.vsm.domain.airport_location.AirportLocationRepository
import id.bluebird.vsm.domain.airport_location.domain.cases.GetListSublocationAirport
import id.bluebird.vsm.domain.airport_location.domain.cases.GetLocationAirport
import id.bluebird.vsm.domain.airport_location.domain.interactor.GetListSublocationAirportCases
import id.bluebird.vsm.domain.airport_location.domain.interactor.GetLocationAirportCases
import id.bluebird.vsm.domain.fleet.FleetRepository
import id.bluebird.vsm.domain.fleet.FleetRepositoryImpl
import id.bluebird.vsm.domain.fleet.domain.cases.*
import id.bluebird.vsm.domain.fleet.domain.interactor.*
import id.bluebird.vsm.domain.location.LocationRepository
import id.bluebird.vsm.domain.location.LocationRepositoryImpl
import id.bluebird.vsm.domain.location.domain.cases.*
import id.bluebird.vsm.domain.location.domain.interactor.*
import id.bluebird.vsm.domain.passenger.QueueReceiptRepository
import id.bluebird.vsm.domain.passenger.QueueReceiptRepositoryImpl
import id.bluebird.vsm.domain.passenger.domain.cases.*
import id.bluebird.vsm.domain.passenger.domain.interactor.*
import id.bluebird.vsm.domain.user.UserRepository
import id.bluebird.vsm.domain.user.UserRepositoryImpl
import id.bluebird.vsm.domain.user.domain.intercator.*
import id.bluebird.vsm.domain.user.domain.usescases.*
import id.bluebird.vsm.feature.airport_fleet.add_by_camera.AddByCameraAirportViewModel
import id.bluebird.vsm.feature.airport_fleet.add_fleet.AddFleetViewModelNonApsh
import id.bluebird.vsm.feature.airport_fleet.assign_location.AssignLocationViewModel
import id.bluebird.vsm.feature.airport_fleet.dialog_request_stock.DialogButtomRequestStockViewModel
import id.bluebird.vsm.feature.airport_fleet.main.FleetNonApshViewModel
import id.bluebird.vsm.feature.airport_fleet.request_list.RequestListViewModel
import id.bluebird.vsm.feature.home.dialog_delete_skipped.DialogDeleteSkippedViewModel
import id.bluebird.vsm.feature.home.dialog_queue_receipt.DialogQueueReceiptViewModel
import id.bluebird.vsm.feature.home.dialog_record_ritase.DialogRecordRitaseViewModel
import id.bluebird.vsm.feature.home.dialog_restore_skipped.DialogRestoreSkippedViewModel
import id.bluebird.vsm.feature.home.dialog_skip_queue.DialogSkipQueueViewModel
import id.bluebird.vsm.feature.home.main.QueuePassengerViewModel
import id.bluebird.vsm.feature.home.queue_search.QueueSearchViewModel
import id.bluebird.vsm.feature.home.queue_ticket.QueueTicketViewModel
import id.bluebird.vsm.feature.home.ritase_fleet.RitaseFleetViewModel
import id.bluebird.vsm.feature.login.LoginViewModel
import id.bluebird.vsm.feature.monitoring.edit_buffer.EditBufferViewModel
import id.bluebird.vsm.feature.monitoring.main.MonitoringViewModel
import id.bluebird.vsm.feature.qrcode.QrCodeViewModel
import id.bluebird.vsm.feature.queue_car_fleet.adapter.AdapterCarFleets
import id.bluebird.vsm.feature.queue_car_fleet.add_by_camera.AddCarFleetByCameraViewModel
import id.bluebird.vsm.feature.queue_car_fleet.add_fleet.AddCarFleetViewModel
import id.bluebird.vsm.feature.queue_car_fleet.depart_fleet.DepartCarFleetViewModel
import id.bluebird.vsm.feature.queue_car_fleet.deposition_fleet.DepositionFleetViewModel
import id.bluebird.vsm.feature.queue_car_fleet.main.QueueCarFleetViewModel
import id.bluebird.vsm.feature.queue_car_fleet.search_fleet.SearchCarFleetViewModel
import id.bluebird.vsm.feature.queue_fleet.adapter.AdapterFleets
import id.bluebird.vsm.feature.queue_fleet.add_by_camera.AddByCameraViewModel
import id.bluebird.vsm.feature.queue_fleet.add_fleet.AddFleetViewModel
import id.bluebird.vsm.feature.queue_fleet.depart_fleet.DepartFleetViewModel
import id.bluebird.vsm.feature.queue_fleet.main.QueueFleetViewModel
import id.bluebird.vsm.feature.queue_fleet.request_fleet.RequestFleetDialogViewModel
import id.bluebird.vsm.feature.queue_fleet.ritase_record.RitaseRecordViewModel
import id.bluebird.vsm.feature.queue_fleet.search_fleet.SearchFleetViewModel
import id.bluebird.vsm.feature.select_location.SelectLocationViewModel
import id.bluebird.vsm.feature.splash.SplashViewModel
import id.bluebird.vsm.feature.user_management.create.CreateUserViewModel
import id.bluebird.vsm.feature.user_management.list.UserManagementViewModel
import id.bluebird.vsm.feature.user_management.search_location.SearchLocationViewModel
import id.bluebird.vsm.pangkalan.logout.LogoutDialogViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.Koin
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

object AppModule {

    private val vmModule = module {
        viewModel { RitaseFleetViewModel(get()) }
        viewModel { LoginViewModel(get()) }
        viewModel { QueueFleetViewModel(get(), get(), get(), get()) }
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
        viewModel { QueueSearchViewModel() }
        viewModel { SelectLocationViewModel(get(), get()) }
        viewModel { AddByCameraViewModel() }
        viewModel { RitaseRecordViewModel(get()) }
        viewModel { SplashViewModel(get()) }
        viewModel { DialogRecordRitaseViewModel(get()) }
        viewModel { QrCodeViewModel(get()) }
        viewModel { FleetNonApshViewModel(get(),get(),get(),get()) }
        viewModel { DialogButtomRequestStockViewModel(get()) }
        viewModel { AddFleetViewModelNonApsh(get(), get()) }
        viewModel { AssignLocationViewModel(get(), get(), get()) }
        viewModel { AddByCameraAirportViewModel(get()) }
        viewModel { RequestListViewModel(get()) }

        viewModel { QueueCarFleetViewModel(get(), get(), get(), get()) }
        viewModel { AddCarFleetByCameraViewModel() }
        viewModel { AddCarFleetViewModel(get(), get(), get()) }
        viewModel { DepartCarFleetViewModel() }
        viewModel { SearchCarFleetViewModel() }
        viewModel { DepositionFleetViewModel(get()) }
    }
    private val userCases = module {
        single<GetUserAssignment> { GetUserAssignmentCases(get()) }
        single<DeleteUser> { DeleteUserCases(get()) }
        single<ForceLogout> { LogoutCasesImpl(get()) }
        single<SearchUser> { SearchUserCases(get()) }
        single<Login> { LoginCaseImpl(get()) }
        single<CreateEditUser> { CreateEditUserCases(get()) }
        single<GetRoles> { GetRolesCases(get()) }
        single<GetUserId> { GetUserIdCases(get()) }
        single<GetUserByIdForAssignment> { GetUserByIdForAssignmentUsesCases(get()) }
        single<ValidateForceUpdate> { ValidateForceUpdateUsesCases(get()) }
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
        single<GetSubLocationQrCode> { GetSubLocationQrCodeCases(get()) }
    }
    private val passengerCases = module {
        single<GetQueueReceipt> { GetQueueReceiptCases(get()) }
        single<TakeQueue> { TakeQueueCases(get()) }
        single<CurrentQueue> { CurrentQueueCases(get()) }
        single<SkipQueue> { SkipQueueCases(get()) }
        single<ListQueueSkipped> { ListQueueSkippedCases(get()) }
        single<ListQueueWaiting> { ListQueueWaitingCases(get()) }
        single<GetWaitingQueue> { GetWaitingQueueCases(get()) }
        single<SearchWaitingQueue> { SearchWaitingQueueCases(get()) }
        single<DeleteSkipped> { DeleteSkippedCases(get()) }
        single<RestoreSkipped> { RestoreSkippedCases(get()) }
        single<CounterBar> { CounterBarCases(get()) }
        single<SearchQueue> { SearchQueueCases(get()) }
    }
    private val airportAssignmentCases = module {
        single<AddFleetAirport> { AddFleetAirportCases(get()) }
        single<AddStockDepart> { AddStockDepartCases(get()) }
        single<AssignFleetTerminalAirport> { AssignFleetTerminalAirportCases(get()) }
        single<DispatchFleetAirport> { DispatchFleetAirportCases(get()) }
        single<GetListFleetTerminal> { GetListFleetTerminalCases(get()) }
        single<GetSubLocationAirport> { GetSubLocationAirportCases(get())}
        single<GetSubLocationStockCountDepart> { GetSubLocationStockCountDepartCases(get()) }
        single<RequestTaxiDepart> { RequestTaxiDepartCases(get()) }
        single<RitaseFleetTerminalAirport> { RitaseFleetTerminalAirportCases(get()) }
        single<GetDetailRequestByLocationAirport> { GetDetailRequestByLocationAirportCases(get()) }
    }

    private val airportLocation = module {
        single<GetLocationAirport> { GetLocationAirportCases(get()) }
        single<GetListSublocationAirport> { GetListSublocationAirportCases(get()) }
    }

    private val repository = module {
        single<UserRepository> { UserRepositoryImpl() }
        single<LocationRepository> { LocationRepositoryImpl() }
        single<FleetRepository> { FleetRepositoryImpl() }
        single<QueueReceiptRepository> { QueueReceiptRepositoryImpl() }
        single<AirportAssignmentRepository> { AirportAssignmentRepositoryImpl() }
        single<AirportLocationRepository> { AirportLocationRepositoryImpl() }
    }
    private val adapter = module {
        single { AdapterFleets() }
        single { AdapterCarFleets() }
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
                    passengerCases,
                    airportAssignmentCases,
                    airportLocation
                )
            )
        }.koin
    }
}