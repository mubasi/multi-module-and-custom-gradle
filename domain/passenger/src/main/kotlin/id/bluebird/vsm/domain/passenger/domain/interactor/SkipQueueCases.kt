package id.bluebird.vsm.domain.passenger.domain.interactor

import id.bluebird.vsm.domain.passenger.QueueReceiptRepository
import id.bluebird.vsm.domain.passenger.SkipQueueState
import id.bluebird.vsm.domain.passenger.domain.cases.SkipQueue
import id.bluebird.vsm.domain.passenger.model.Queue
import id.bluebird.vsm.domain.passenger.model.SkipQueueResult
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