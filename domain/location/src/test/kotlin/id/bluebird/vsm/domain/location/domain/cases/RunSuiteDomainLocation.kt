package id.bluebird.vsm.domain.location.domain.cases

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasses(
    GetLocationsCasesTest::class,
    GetLocationsWithSubUseCasesTest::class,
    GetSubLocationByLocationIdCasesTest::class,
    UpdateBufferCasesTest::class,
    GetSubLocationQrCodeCasesTest::class
)

@ExperimentalCoroutinesApi
class RunSuiteDomainLocation