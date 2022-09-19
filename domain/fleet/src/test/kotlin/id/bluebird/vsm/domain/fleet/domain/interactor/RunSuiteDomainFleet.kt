package id.bluebird.vsm.domain.fleet.domain.interactor

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasses(
    AddFleetUseCasesTest::class,
    DepartFleetUseCasesTest::class,
    GetCountCasesTest::class,
    GetListFleetUseCasesTest::class,
    RequestFleetUseCasesTest::class,
    SearchFleetUseCasesTest::class
)

@ExperimentalCoroutinesApi
class RunSuiteDomainFleet