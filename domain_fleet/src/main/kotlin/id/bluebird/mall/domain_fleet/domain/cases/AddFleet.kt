package id.bluebird.mall.domain_fleet.domain.cases

import id.bluebird.mall.domain_fleet.AddFleetState
import kotlinx.coroutines.flow.Flow

interface AddFleet {
    operator fun invoke(
        fleetNumber: String,
        subLocationId: Long
    ): Flow<AddFleetState>
}