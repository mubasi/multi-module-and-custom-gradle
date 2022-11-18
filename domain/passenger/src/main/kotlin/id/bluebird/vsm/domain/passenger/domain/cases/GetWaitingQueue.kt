package id.bluebird.vsm.domain.passenger.domain.cases

import id.bluebird.vsm.domain.passenger.WaitingQueueState
import kotlinx.coroutines.flow.Flow

interface GetWaitingQueue {
    operator fun invoke(locationId: Long): Flow<WaitingQueueState>
}