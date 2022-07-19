package id.bluebird.mall.domain_fleet.domain.cases

import id.bluebird.mall.domain_fleet.GetListFleetState
import kotlinx.coroutines.flow.Flow

interface GetListFleet {
    operator fun invoke(subLocationId: Long): Flow<GetListFleetState>
}