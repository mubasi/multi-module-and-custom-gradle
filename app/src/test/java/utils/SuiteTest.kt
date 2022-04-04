package utils

import id.bluebird.mall.officer.case.LoginCaseTest
import id.bluebird.mall.officer.viewModel.LoginViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasses(
    LoginViewModelTest::class,
    LoginCaseTest::class
)
@ExperimentalCoroutinesApi
class SuiteTest