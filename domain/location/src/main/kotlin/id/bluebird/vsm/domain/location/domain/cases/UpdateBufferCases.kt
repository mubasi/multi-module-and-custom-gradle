package id.bluebird.vsm.domain.location.domain.cases

import id.bluebird.vsm.domain.location.LocationDomainState
import id.bluebird.vsm.domain.location.LocationRepository
import id.bluebird.vsm.domain.location.domain.interactor.UpdateBuffer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class UpdateBufferCases(
    private val locationRepository: LocationRepository
): UpdateBuffer {
    override fun invoke(subLocationId: Long, value: Int): Flow<LocationDomainState<String>> =
        flow {
            val result = locationRepository.updateBuffer(subLocationId, value.toLong())
                .flowOn(Dispatchers.IO)
                .singleOrNull() ?: throw NullPointerException()

            emit(LocationDomainState.Success(result.message))
        }
}