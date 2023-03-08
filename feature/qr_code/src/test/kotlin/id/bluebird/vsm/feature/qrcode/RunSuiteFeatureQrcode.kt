package id.bluebird.vsm.feature.qrcode

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite


@Suite
@SelectClasses(
    QrCodeViewModelTest::class,
)

@ExperimentalCoroutinesApi
class RunSuiteFeatureQrcode