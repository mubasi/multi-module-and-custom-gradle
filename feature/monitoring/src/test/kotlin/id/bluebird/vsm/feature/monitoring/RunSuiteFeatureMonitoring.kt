package id.bluebird.vsm.feature.monitoring

import id.bluebird.vsm.feature.monitoring.main.MonitoringViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasses(
    MonitoringViewModelTest::class
)

@ExperimentalCoroutinesApi
class RunSuiteFeatureMonitoring