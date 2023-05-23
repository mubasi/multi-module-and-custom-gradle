package id.bluebird.vsm.domain.airport_assignment.domain.interactor

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite


@Suite
@SelectClasses(
    AddFleetAirportCasesTest::class,
    AddStockDepartCasesTest::class,
    AssignFleetTerminalAirportCasesTest::class,
    DispatchFleetAirportCasesTest::class,
    GetListFleetTerminalCasesTest::class,
    GetSubLocationStockCountDepartCasesTest::class,
    RequestTaxiDepartCasesTest::class,
    RitaseFleetTerminalAirportCasesTest::class,
    GetSubLocationAirportCasesTest::class
)

@ExperimentalCoroutinesApi
class RunSuiteDomainAirportAssigment