package id.bluebird.vsm.feature.queue_car_fleet

import id.bluebird.vsm.feature.queue_car_fleet.add_by_camera.AddCarFleetByCameraViewModelTest
import id.bluebird.vsm.feature.queue_car_fleet.add_fleet.AddCarFleetViewModelTest
import id.bluebird.vsm.feature.queue_car_fleet.depart_fleet.DepartCarFleetViewModelTest
import id.bluebird.vsm.feature.queue_car_fleet.main.QueueCarFleetViewModelTest
import id.bluebird.vsm.feature.queue_car_fleet.request_fleet.RequestCarFleetDialogViewModelTest
import id.bluebird.vsm.feature.queue_car_fleet.search_fleet.SearchCarFleetViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasses(
    AddCarFleetViewModelTest::class,
    DepartCarFleetViewModelTest::class,
    QueueCarFleetViewModelTest::class,
    RequestCarFleetDialogViewModelTest::class,
    SearchCarFleetViewModelTest::class,
    AddCarFleetByCameraViewModelTest::class
)

@ExperimentalCoroutinesApi
class RunSuiteFeatureQueueCarFleet