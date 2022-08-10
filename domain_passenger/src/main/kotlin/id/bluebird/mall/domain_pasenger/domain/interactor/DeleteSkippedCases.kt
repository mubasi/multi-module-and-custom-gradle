package id.bluebird.mall.domain_pasenger.domain.interactor

import id.bluebird.mall.domain_pasenger.DeleteSkippedState
import id.bluebird.mall.domain_pasenger.QueueReceiptRepository
import id.bluebird.mall.domain_pasenger.TakeQueueState
import id.bluebird.mall.domain_pasenger.domain.cases.DeleteSkipped
import id.bluebird.mall.domain_pasenger.domain.cases.TakeQueue
import id.bluebird.mall.domain_pasenger.model.Queue
import id.bluebird.mall.domain_pasenger.model.QueueResult
import id.bluebird.mall.domain_pasenger.model.TakeQueueResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull
import java.lang.NullPointerException

class DeleteSkippedCases (private val queueReceiptRepository: QueueReceiptRepository) :
    DeleteSkipped {
    override fun invoke(
        queueId: Long,
        queueType: Long,
        locationId: Long,
        queueNumber: String,
        subLocationId: Long,
        fleetNumber: String,
    ): Flow<DeleteSkippedState> =
        flow {
            val response = queueReceiptRepository.deleteSkippedQueue(
                queueId,
                queueType,
                locationId,
                queueNumber,
                subLocationId,
                fleetNumber
            )
                .flowOn(Dispatchers.IO)
                .singleOrNull() ?: throw NullPointerException()
            val queue = Queue(
                response.queue.id,
                response.queue.number,
                response.queue.createdAt,
                response.queue.message,
                response.queue.currentQueue,
                response.queue.totalQueue,
                response.queue.timeOrder,
                response.queue.subLocationId
            )
            val queueCurrentResult = QueueResult(response.message, response.queueType, queue)
            emit(DeleteSkippedState.Success(queueCurrentResult))
        }
}