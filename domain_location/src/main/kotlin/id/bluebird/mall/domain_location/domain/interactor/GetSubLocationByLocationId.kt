package id.bluebird.mall.domain_location.domain.interactor

import id.bluebird.mall.domain_location.LocationDomainState
import id.bluebird.mall.domain_location.model.SubLocationResult
import kotlinx.coroutines.flow.Flow

interface GetSubLocationByLocationId {
    operator fun invoke(id: Long): Flow<LocationDomainState<List<SubLocationResult>>>
}