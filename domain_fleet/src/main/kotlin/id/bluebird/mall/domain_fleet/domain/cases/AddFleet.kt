package id.bluebird.mall.domain_fleet.domain.cases

import kotlinx.coroutines.flow.Flow

interface AddFleet {
    operator fun invoke(
        fleetNumber: String,
        subLocationId: Long
    ): Flow<String>
}