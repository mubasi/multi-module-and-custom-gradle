package id.bluebird.vsm.feature.user_management

import id.bluebird.vsm.feature.user_management.create.CreateUserViewModelTest
import id.bluebird.vsm.feature.user_management.list.UserManagementViewModelTest
import id.bluebird.vsm.feature.user_management.search_location.SearchLocationViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasses(
    CreateUserViewModelTest::class,
    UserManagementViewModelTest::class,
    SearchLocationViewModelTest::class
)

@ExperimentalCoroutinesApi
class RunSuiteFeatureUser