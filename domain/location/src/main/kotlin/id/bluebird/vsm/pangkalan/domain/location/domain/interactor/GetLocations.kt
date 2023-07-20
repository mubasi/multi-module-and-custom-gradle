package id.bluebird.vsm.domain.location.domain.interactor

import id.bluebird.vsm.domain.location.LocationDomainState
import id.bluebird.vsm.domain.location.model.LocationResult
import kotlinx.coroutines.flow.Flow

interface GetLocations {
    operator fun invoke(): Flow<LocationDomainState<List<LocationResult>>>
}