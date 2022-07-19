import id.bluebird.mall.domain_fleet.domain.interactor.GetCountCasesTest
import id.bluebird.mall.domain_fleet.domain.interactor.GetListFleetUseCasesTest
import id.bluebird.mall.domain_fleet.domain.interactor.RequestFleetUseCasesTest
import id.bluebird.mall.domain_fleet.domain.interactor.SearchFleetUseCasesTest
import id.bluebird.mall.feature_queue_fleet.main.QueueFleetViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasses(
    QueueFleetViewModelTest::class,
    GetCountCasesTest::class,
    GetListFleetUseCasesTest::class,
    RequestFleetUseCasesTest::class,
    SearchFleetUseCasesTest::class
)
@ExperimentalCoroutinesApi
class RunSuiteTest