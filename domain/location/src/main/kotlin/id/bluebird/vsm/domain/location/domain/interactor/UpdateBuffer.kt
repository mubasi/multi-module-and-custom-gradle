package id.bluebird.vsm.domain.location.domain.interactor

import id.bluebird.vsm.domain.location.LocationDomainState
import kotlinx.coroutines.flow.Flow

interface UpdateBuffer {
    operator fun invoke(subLocationId: Long, value: Int): Flow<LocationDomainState<String>>
}