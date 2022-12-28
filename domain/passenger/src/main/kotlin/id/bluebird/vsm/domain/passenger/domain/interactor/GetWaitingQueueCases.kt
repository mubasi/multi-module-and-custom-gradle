package id.bluebird.vsm.domain.passenger.domain.interactor

import id.bluebird.vsm.domain.passenger.QueueReceiptRepository
import id.bluebird.vsm.domain.passenger.WaitingQueueState
import id.bluebird.vsm.domain.passenger.domain.cases.GetWaitingQueue
import id.bluebird.vsm.domain.passenger.model.Queue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class GetWaitingQueueCases(private val queueReceiptRepository: QueueReceiptRepository): GetWaitingQueue {
    override fun invoke(locationId: Long, subLocationId: Long): Flow<WaitingQueueState> = flow {
        val response = queueReceiptRepository.getWaitingQueue(locationId, subLocationId)
            .flowOn(Dispatchers.IO)
            .singleOrNull() ?: throw NullPointerException()

        if (response.count < 1) {
            emit(WaitingQueueState.EmptyResult)
        } else {
            val result = response.waitingList.map {
                Queue(
                    id = it.id,
                    number = it.number,
                    createdAt = it.createdAt,
                    message = it.message,
                    currentQueue = it.currentQueue,
                    totalQueue = it.totalQueue,
                    timeOrder = it.timeOrder,
                    subLocationId = it.subLocationId
                )
            }
            emit(WaitingQueueState.Success(result))
        }
    }
}