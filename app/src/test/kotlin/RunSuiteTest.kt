import id.bluebird.vsm.domain.fleet.domain.interactor.GetCountCasesTest
import id.bluebird.vsm.domain.fleet.domain.interactor.GetListFleetUseCasesTest
import id.bluebird.vsm.domain.fleet.domain.interactor.RequestFleetUseCasesTest
import id.bluebird.vsm.domain.fleet.domain.interactor.SearchFleetUseCasesTest
import id.bluebird.vsm.feature.queue_fleet.main.QueueFleetViewModelTest
import id.bluebird.vsm.feature.queue_fleet.search_fleet.SearchFleetViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasses(
    QueueFleetViewModelTest::class,
    SearchFleetViewModelTest::class,
    GetCountCasesTest::class,
    GetListFleetUseCasesTest::class,
    RequestFleetUseCasesTest::class,
    SearchFleetUseCasesTest::class
)
@ExperimentalCoroutinesApi
class RunSuiteTest