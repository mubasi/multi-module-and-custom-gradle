package id.bluebird.vsm.domain.fleet.domain.cases

import id.bluebird.vsm.domain.fleet.GetListFleetState
import kotlinx.coroutines.flow.Flow

interface GetListFleet {
    operator fun invoke(subLocationId: Long): Flow<GetListFleetState>
}