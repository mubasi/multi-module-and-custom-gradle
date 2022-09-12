package id.bluebird.vsm.domain.passenger.domain.cases

import id.bluebird.vsm.domain.passenger.SkipQueueState
import kotlinx.coroutines.flow.Flow

interface SkipQueue {
    operator fun invoke(
        queueId :  Long,
        locationId :  Long,
        subLocationId :  Long
    ) : Flow<SkipQueueState>
}