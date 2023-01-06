package id.bluebird.vsm.domain.fleet.domain.cases

import id.bluebird.vsm.domain.fleet.AddFleetState
import kotlinx.coroutines.flow.Flow

interface AddFleet {
    operator fun invoke(
        fleetNumber: String,
        subLocationId: Long,
        locationId: Long
    ): Flow<AddFleetState>
}