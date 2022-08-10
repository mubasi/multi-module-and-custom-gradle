package id.bluebird.mall.domain_location.domain.interactor

import id.bluebird.mall.domain_location.LocationDomainState
import kotlinx.coroutines.flow.Flow

interface UpdateBuffer {
    operator fun invoke(subLocationId: Long, value: Int): Flow<LocationDomainState<String>>
}