package id.bluebird.vsm.domain.fleet.domain.cases

import id.bluebird.vsm.domain.fleet.SearchFleetState
import kotlinx.coroutines.flow.Flow

interface SearchFleet {
    operator fun invoke(param: String?): Flow<SearchFleetState>
}