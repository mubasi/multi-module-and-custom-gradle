package id.bluebird.vsm.domain.fleet.domain.interactor

import id.bluebird.vsm.domain.fleet.FleetRepository
import id.bluebird.vsm.domain.fleet.GetCountState
import id.bluebird.vsm.domain.fleet.domain.cases.GetCount
import id.bluebird.vsm.domain.fleet.model.CountResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull
import java.time.Instant

class GetCountCases(private val fleetRepository: FleetRepository) : GetCount {

    override fun invoke(
        subLocationId: Long,
        locationId: Long
    ): Flow<GetCountState> = flow {
        val response = fleetRepository.getCount(
            subLocation = subLocationId,
            locationId = locationId,
            todayEpoch = Instant.ofEpochMilli(System.currentTimeMillis()).epochSecond
        )
            .flowOn(Dispatchers.IO)
            .singleOrNull() ?: throw NullPointerException()
        val countResult = CountResult(response.stock, response.ritase, response.request)
        emit(GetCountState.Success(countResult))
    }
}