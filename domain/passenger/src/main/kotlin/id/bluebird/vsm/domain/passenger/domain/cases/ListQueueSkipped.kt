package id.bluebird.vsm.domain.passenger.domain.cases

import id.bluebird.vsm.domain.passenger.ListQueueSkippedState
import kotlinx.coroutines.flow.Flow

interface ListQueueSkipped {
    operator fun invoke(
        locationId :  Long,
        subLocationId: Long,
    ) : Flow<ListQueueSkippedState>
}