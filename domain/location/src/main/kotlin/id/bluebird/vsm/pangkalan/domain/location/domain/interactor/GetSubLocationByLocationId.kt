package id.bluebird.vsm.domain.location.domain.interactor

import id.bluebird.vsm.domain.location.LocationDomainState
import id.bluebird.vsm.domain.location.model.SubLocationResult
import kotlinx.coroutines.flow.Flow

interface GetSubLocationByLocationId {
    operator fun invoke(id: Long): Flow<LocationDomainState<List<SubLocationResult>>>
}