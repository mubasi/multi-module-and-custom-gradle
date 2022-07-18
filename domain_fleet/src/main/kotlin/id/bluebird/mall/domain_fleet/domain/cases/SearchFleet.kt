package id.bluebird.mall.domain_fleet.domain.cases

import id.bluebird.mall.domain_fleet.SearchFleetState
import kotlinx.coroutines.flow.Flow

interface SearchFleet {
    operator fun invoke(param: String?): Flow<SearchFleetState>
}