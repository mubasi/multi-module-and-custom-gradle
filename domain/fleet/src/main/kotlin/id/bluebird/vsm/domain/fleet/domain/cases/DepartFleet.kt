package id.bluebird.vsm.domain.fleet.domain.cases

import id.bluebird.vsm.domain.fleet.DepartFleetState
import kotlinx.coroutines.flow.Flow

interface DepartFleet {
    operator fun invoke(
        locationId: Long,
        subLocationId: Long,
        fleetNumber: String,
        isWithPassenger: Boolean,
        departFleetItems: List<Long>,
        queueNumber: String
    ): Flow<DepartFleetState>
}