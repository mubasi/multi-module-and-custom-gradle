package id.bluebird.vsm.domain.passenger.domain.cases

import id.bluebird.vsm.domain.passenger.GetCurrentQueueState
import kotlinx.coroutines.flow.Flow

interface GetCurrentQueue {
    operator fun invoke(): Flow<GetCurrentQueueState>
}