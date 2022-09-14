package id.bluebird.vsm.domain.passenger.domain.cases

import id.bluebird.vsm.domain.passenger.GetCurrentQueueState
import kotlinx.coroutines.flow.Flow

interface CurrentQueue {
    operator fun invoke(
        locationId :  Long,
    ) : Flow<GetCurrentQueueState>
}