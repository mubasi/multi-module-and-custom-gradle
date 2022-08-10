package id.bluebird.mall.domain_pasenger.domain.interactor

import id.bluebird.mall.domain_pasenger.QueueReceiptRepository
import id.bluebird.mall.domain_pasenger.RestoreSkippedState
import id.bluebird.mall.domain_pasenger.domain.cases.RestoreSkipped
import id.bluebird.mall.domain_pasenger.model.Queue
import id.bluebird.mall.domain_pasenger.model.QueueResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull
import java.lang.NullPointerException

class RestoreSkippedCases (private val queueReceiptRepository: QueueReceiptRepository) :
    RestoreSkipped {
    override fun invoke(
        queueId: Long,
        queueType: Long,
        locationId: Long,
        queueNumber: String,
        subLocationId: Long,
        fleetNumber: String,
    ): Flow<RestoreSkippedState> =
        flow {
            val response = queueReceiptRepository.restoreSkippedQueue(
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

            val result = QueueResult(response.message, response.queueType, queue)
            emit(RestoreSkippedState.Success(result))
        }
}