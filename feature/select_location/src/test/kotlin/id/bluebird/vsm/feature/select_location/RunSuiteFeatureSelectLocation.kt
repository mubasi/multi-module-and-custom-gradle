package id.bluebird.vsm.feature.select_location

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasses(
    SelectLocationViewModelTest::class,
)

@ExperimentalCoroutinesApi
class RunSuiteFeatureSelectLocation