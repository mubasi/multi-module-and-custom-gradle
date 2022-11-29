package id.bluebird.vsm.domain.passenger.domain.cases

import id.bluebird.vsm.domain.passenger.WaitingQueueState
import kotlinx.coroutines.flow.Flow

interface SearchWaitingQueue {
    operator fun invoke(queueNumber: String, locationId: Long, subLocationId: Long): Flow<WaitingQueueState>
}