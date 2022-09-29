package id.bluebird.vsm.feature.queue_fleet

import id.bluebird.vsm.feature.queue_fleet.add_by_camera.AddByCameraViewModelTest
import id.bluebird.vsm.feature.queue_fleet.add_fleet.AddFleetViewModelTest
import id.bluebird.vsm.feature.queue_fleet.depart_fleet.DepartFleetViewModelTest
import id.bluebird.vsm.feature.queue_fleet.main.QueueFleetViewModelTest
import id.bluebird.vsm.feature.queue_fleet.request_fleet.RequestFleetDialogViewModelTest
import id.bluebird.vsm.feature.queue_fleet.search_fleet.SearchFleetViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasses(
    AddFleetViewModelTest::class,
    DepartFleetViewModelTest::class,
    QueueFleetViewModelTest::class,
    RequestFleetDialogViewModelTest::class,
    SearchFleetViewModelTest::class,
    AddByCameraViewModelTest::class
)

@ExperimentalCoroutinesApi
class RunSuiteFeatureQueueFleet