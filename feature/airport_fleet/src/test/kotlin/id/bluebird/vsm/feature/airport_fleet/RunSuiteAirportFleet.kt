package id.bluebird.vsm.feature.airport_fleet

import id.bluebird.vsm.feature.airport_fleet.add_by_camera.AddByCameraAirportViewModelTest
import id.bluebird.vsm.feature.airport_fleet.add_fleet.AddFleetViewModelNonApshTest
import id.bluebird.vsm.feature.airport_fleet.assign_location.AssignLocationViewModelTest
import id.bluebird.vsm.feature.airport_fleet.dialog_request_stock.DialogButtomRequestStockViewModelTest
import id.bluebird.vsm.feature.airport_fleet.main.FleetNonApshViewModelTest
import id.bluebird.vsm.feature.airport_fleet.request_list.RequestListViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasses(
    AddByCameraAirportViewModelTest::class,
    AddFleetViewModelNonApshTest::class,
    AssignLocationViewModelTest::class,
    DialogButtomRequestStockViewModelTest::class,
    FleetNonApshViewModelTest::class,
    RequestListViewModelTest::class
)

@ExperimentalCoroutinesApi
class RunSuiteAirportFleet