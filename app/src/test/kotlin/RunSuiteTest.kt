import id.bluebird.vsm.domain.fleet.domain.interactor.RunSuiteDomainFleet
import id.bluebird.vsm.domain.location.domain.cases.RunSuiteDomainLocation
import id.bluebird.vsm.domain.passenger.domain.interactor.RunSuiteDomainPassenger
import id.bluebird.vsm.domain.user.domain.usescases.RunSuiteDomainUser
import id.bluebird.vsm.feature.select_location.RunSuiteFeatureSelectLocation
import id.bluebird.vsm.feature.user_management.RunSuiteFeatureUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasses(
    RunSuiteDomainPassenger::class,
    RunSuiteDomainUser::class,
    RunSuiteDomainFleet::class,
    RunSuiteDomainLocation::class,
    RunSuiteFeatureUser::class,
    RunSuiteFeatureSelectLocation::class
)
@ExperimentalCoroutinesApi
class RunSuiteTest