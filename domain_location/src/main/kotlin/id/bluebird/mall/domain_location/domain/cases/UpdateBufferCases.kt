package id.bluebird.mall.domain_location.domain.cases

import id.bluebird.mall.domain_location.LocationDomainState
import id.bluebird.mall.domain_location.LocationRepository
import id.bluebird.mall.domain_location.domain.interactor.UpdateBuffer
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