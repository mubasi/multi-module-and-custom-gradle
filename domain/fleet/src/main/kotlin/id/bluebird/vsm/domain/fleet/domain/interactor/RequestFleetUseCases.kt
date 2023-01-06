package id.bluebird.vsm.domain.fleet.domain.interactor

import id.bluebird.vsm.domain.fleet.FleetRepository
import id.bluebird.vsm.domain.fleet.RequestState
import id.bluebird.vsm.domain.fleet.domain.cases.RequestFleet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class RequestFleetUseCases(private val fleetRepository: FleetRepository) : RequestFleet {

    private lateinit var flowCollector: FlowCollector<RequestState>
    override fun invoke(count: Long, subLocationId: Long, locationId: Long): Flow<RequestState> =
        flow {
            flowCollector = this
            validate(count, subLocationId)
            val result = fleetRepository.requestFleet(
                count = count,
                locationId = locationId,
                subLocation = subLocationId
            )
                .flowOn(Dispatchers.IO)
                .singleOrNull() ?: throw NullPointerException()
            emit(RequestState.Success(result.requestCount))
        }

    private suspend fun validate(count: Long, subLocationId: Long) {
        if (count < 1) {
            flowCollector.emit(RequestState.CountInvalid)
        }
        if (subLocationId < 1) {
            flowCollector.emit(RequestState.SubLocationInvalid)
        }
    }
}