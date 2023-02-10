package id.bluebird.vsm.domain.user.domain.usescases

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasses(
    CreateEditUserCasesTest::class,
    DeleteUserCasesTest::class,
    GetRolesCasesTest::class,
    GetUserIdCasesTest::class,
    LoginCaseImplTest::class,
    LogoutCasesImplTest::class,
    SearchUserCasesTest::class,
    ValidateForceUpdateUsesCasesTest::class
)
@ExperimentalCoroutinesApi
class RunSuiteDomainUser