import id.bluebird.vsm.domain.passenger.domain.interactor.RunSuiteDomainPassenger
import id.bluebird.vsm.domain.user.domain.usescases.RunSuiteDomainUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasses(
    RunSuiteDomainPassenger::class,
    RunSuiteDomainUser::class,
)
@ExperimentalCoroutinesApi
class RunSuiteTest