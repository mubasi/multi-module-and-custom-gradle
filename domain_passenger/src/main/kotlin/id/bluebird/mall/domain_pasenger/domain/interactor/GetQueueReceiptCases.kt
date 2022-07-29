package id.bluebird.mall.domain_pasenger.domain.interactor

import android.util.Log
import id.bluebird.mall.domain_pasenger.GetQueueReceiptState
import id.bluebird.mall.domain_pasenger.QueueReceiptRepository
import id.bluebird.mall.domain_pasenger.domain.cases.GetQueueReceipt
import id.bluebird.mall.domain_pasenger.model.Queue
import id.bluebird.mall.domain_pasenger.model.QueueResult
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