package id.bluebird.mall.domain_fleet.domain.cases

import id.bluebird.mall.domain_fleet.RequestState
import kotlinx.coroutines.flow.Flow

interface RequestFleet {
    operator fun invoke(count: Long, subLocationId: Long): Flow<RequestState>
}