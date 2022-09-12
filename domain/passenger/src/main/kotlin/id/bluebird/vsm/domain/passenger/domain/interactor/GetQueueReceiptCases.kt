package id.bluebird.vsm.domain.passenger.domain.interactor

import id.bluebird.vsm.domain.passenger.GetQueueReceiptState
import id.bluebird.vsm.domain.passenger.QueueReceiptRepository
import id.bluebird.vsm.domain.passenger.domain.cases.GetQueueReceipt
import id.bluebird.vsm.domain.passenger.model.Queue
import id.bluebird.vsm.domain.passenger.model.QueueResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull
import java.lang.NullPointerException

class GetQueueReceiptCases (private val queueReceiptRepository: QueueReceiptRepository) :
    GetQueueReceipt {
    override fun invoke(
        queueId: Long,
        queueType: Long,
        locationId: Long,
        queueNumber: String,
        subLocationId: Long,
        fleetNumber: String,
    ): Flow<GetQueueReceiptState> =
        flow {
            val response = queueReceiptRepository.getQueue(
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
            emit(GetQueueReceiptState.Success(queueCurrentResult))
        }
}