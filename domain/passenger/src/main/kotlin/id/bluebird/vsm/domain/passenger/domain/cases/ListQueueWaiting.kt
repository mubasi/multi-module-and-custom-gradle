package id.bluebird.vsm.domain.passenger.domain.cases

import id.bluebird.vsm.domain.passenger.ListQueueWaitingState
import kotlinx.coroutines.flow.Flow

interface ListQueueWaiting {
    operator fun invoke(
        locationId :  Long,
        subLocationId: Long,
    ) : Flow<ListQueueWaitingState>
}