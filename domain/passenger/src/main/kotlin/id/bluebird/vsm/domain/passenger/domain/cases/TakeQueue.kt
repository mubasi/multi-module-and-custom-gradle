package id.bluebird.vsm.domain.passenger.domain.cases

import id.bluebird.vsm.domain.passenger.TakeQueueState
import kotlinx.coroutines.flow.Flow

interface TakeQueue {
    operator fun invoke(
        queueId: Long,
        queueType: Long,
        locationId :  Long,
        queueNumber: String,
        subLocationId: Long,
        fleetNumber: String
    ) : Flow<TakeQueueState>
}