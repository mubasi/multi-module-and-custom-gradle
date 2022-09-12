package id.bluebird.vsm.domain.fleet.domain.cases

import id.bluebird.vsm.domain.fleet.GetCountState
import kotlinx.coroutines.flow.Flow

interface GetCount {
    operator fun invoke(subLocationId: Long): Flow<GetCountState>
}