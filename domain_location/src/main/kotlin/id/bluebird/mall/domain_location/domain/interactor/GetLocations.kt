package id.bluebird.mall.domain_location.domain.interactor

import id.bluebird.mall.domain_location.LocationDomainState
import id.bluebird.mall.domain_location.model.LocationResult
import kotlinx.coroutines.flow.Flow

interface GetLocations {
    operator fun invoke(): Flow<LocationDomainState<List<LocationResult>>>
}