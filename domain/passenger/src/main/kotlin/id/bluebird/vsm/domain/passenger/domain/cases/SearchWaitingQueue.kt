package id.bluebird.vsm.domain.passenger.domain.cases

import id.bluebird.vsm.domain.passenger.WaitingQueueState
import id.bluebird.vsm.domain.passenger.model.Queue
import kotlinx.coroutines.flow.Flow

interface SearchWaitingQueue {
    operator fun invoke(queueNumber: String, subLocationId: Long): Flow<WaitingQueueState>
}