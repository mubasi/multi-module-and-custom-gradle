package id.bluebird.mall.domain_fleet.domain.cases

import id.bluebird.mall.domain_fleet.DepartFleetState
import kotlinx.coroutines.flow.Flow

interface DepartFleet {
    operator fun invoke(
        subLocationId: Long,
        fleetNumber: String,
        isWithPassenger: Boolean,
        departFleetItems: List<Long>,
        queueNumber: String
    ): Flow<DepartFleetState>
}