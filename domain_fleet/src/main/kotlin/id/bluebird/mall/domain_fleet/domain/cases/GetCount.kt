package id.bluebird.mall.domain_fleet.domain.cases

import id.bluebird.mall.domain_fleet.GetCountState
import kotlinx.coroutines.flow.Flow

interface GetCount {
    operator fun invoke(subLocationId: Long): Flow<GetCountState>
}