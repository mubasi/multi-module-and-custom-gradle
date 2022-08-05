package id.bluebird.mall.domain_pasenger.domain.cases

import id.bluebird.mall.domain_pasenger.WaitingQueueState
import id.bluebird.mall.domain_pasenger.model.Queue
import kotlinx.coroutines.flow.Flow

interface SearchWaitingQueue {
    operator fun invoke(queueNumber: String, subLocationId: Long): Flow<WaitingQueueState<List<Queue>>>
}