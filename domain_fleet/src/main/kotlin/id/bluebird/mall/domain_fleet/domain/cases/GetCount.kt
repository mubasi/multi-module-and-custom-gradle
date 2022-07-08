package id.bluebird.mall.domain_fleet.domain.cases

import id.bluebird.mall.domain_fleet.DomainFleetState
import id.bluebird.mall.domain_fleet.model.CountResult
import kotlinx.coroutines.flow.Flow

interface GetCount {
    operator fun invoke(subLocationId: Long): Flow<DomainFleetState<CountResult>>
}