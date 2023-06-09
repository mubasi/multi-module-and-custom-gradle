import id.bluebird.vsm.domain.airport_assignment.domain.interactor.RunSuiteDomainAirportAssigment
import id.bluebird.vsm.domain.airport_location.domain.interactor.RunSuiteDomainAirportLocation
import id.bluebird.vsm.domain.fleet.domain.interactor.RunSuiteDomainFleet
import id.bluebird.vsm.domain.location.domain.cases.RunSuiteDomainLocation
import id.bluebird.vsm.domain.passenger.domain.interactor.RunSuiteDomainPassenger
import id.bluebird.vsm.domain.user.domain.usescases.RunSuiteDomainUser
import id.bluebird.vsm.feature.airport_fleet.RunSuiteAirportFleet
import id.bluebird.vsm.feature.home.RunSuiteFeatureQueuePassenger
import id.bluebird.vsm.feature.monitoring.RunSuiteFeatureMonitoring
import id.bluebird.vsm.feature.qrcode.RunSuiteFeatureQrcode
import id.bluebird.vsm.feature.queue_car_fleet.RunSuiteFeatureQueueCarFleet
import id.bluebird.vsm.feature.queue_fleet.RunSuiteFeatureQueueFleet
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
    RunSuiteFeatureQueueFleet::class,
    RunSuiteFeatureUser::class,
    RunSuiteFeatureQueuePassenger::class,
    RunSuiteFeatureMonitoring::class,
    RunSuiteFeatureQrcode::class,
    RunSuiteDomainAirportAssigment::class,
    RunSuiteDomainAirportLocation::class,
    RunSuiteAirportFleet::class,
    RunSuiteFeatureQueueCarFleet::class
)
@ExperimentalCoroutinesApi
class RunSuiteTest