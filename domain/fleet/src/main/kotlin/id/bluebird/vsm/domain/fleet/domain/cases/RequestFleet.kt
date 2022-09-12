package id.bluebird.vsm.domain.fleet.domain.cases

import id.bluebird.vsm.domain.fleet.RequestState
import kotlinx.coroutines.flow.Flow

interface RequestFleet {
    operator fun invoke(count: Long, subLocationId: Long): Flow<RequestState>
}