package id.bluebird.vsm.domain.airport_location.domain.interactor

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite


@Suite
@SelectClasses(
    GetLocationAirportCasesTest::class,
    GetListSublocationAirportCasesTest::class
)

@ExperimentalCoroutinesApi
class RunSuiteDomainAirportLocation