package id.bluebird.mall.domain_pasenger.domain.interactor

import id.bluebird.mall.domain_pasenger.QueueReceiptRepository
import id.bluebird.mall.domain_pasenger.SkipQueueState
import id.bluebird.mall.domain_pasenger.TakeQueueState
import id.bluebird.mall.domain_pasenger.domain.cases.SkipQueue
import id.bluebird.mall.domain_pasenger.model.Queue
import id.bluebird.mall.domain_pasenger.model.SkipQueueResult
import id.bluebird.mall.domain_pasenger.model.TakeQueueResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull
import java.lang.NullPointerException

class SkipQueueCases (private val queueReceiptRepository: QueueReceiptRepository) :
    SkipQueue {
    override fun invoke(
        queueId: Long,
        locationId: Long,
        subLocationId: Long,
    ): Flow<SkipQueueState> =
        flow {
            val response = queueReceiptRepository.skipQueue(
                queueId,
                locationId,
                subLocationId
            )
                .flowOn(Dispatchers.IO)
                .singleOrNull() ?: throw NullPointerException()

            val queue = Queue(
                response.nextQueue.id,
                response.nextQueue.number,
                response.nextQueue.createdAt,
                response.nextQueue.message,
                response.nextQueue.currentQueue,
                response.nextQueue.totalQueue,
                response.nextQueue.timeOrder,
                response.nextQueue.subLocationId
            )

            val queueCurrentResult = SkipQueueResult(response.skippedId, queue)
            emit(SkipQueueState.Success(queueCurrentResult))
        }
}