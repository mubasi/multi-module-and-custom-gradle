package id.bluebird.vsm.domain.passenger.domain.cases

import id.bluebird.vsm.domain.passenger.RestoreSkippedState
import kotlinx.coroutines.flow.Flow

interface RestoreSkipped {
    operator fun invoke(
        queueId: Long,
        queueType: Long,
        locationId :  Long,
        queueNumber: String,
        subLocationId: Long,
        fleetNumber: String
    ) : Flow<RestoreSkippedState>
}