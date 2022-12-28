package id.bluebird.vsm.domain.passenger.domain.interactor

import id.bluebird.vsm.domain.passenger.ListQueueWaitingState
import id.bluebird.vsm.domain.passenger.QueueReceiptRepository
import id.bluebird.vsm.domain.passenger.domain.cases.ListQueueWaiting
import id.bluebird.vsm.domain.passenger.model.ListQueueResult
import id.bluebird.vsm.domain.passenger.model.Queue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class ListQueueWaitingCases(private val queueReceiptRepository: QueueReceiptRepository) :
ListQueueWaiting{
    override fun invoke(locationId: Long, subLocationId: Long):
            Flow<ListQueueWaitingState> = flow {
        val response = queueReceiptRepository.listQueueWaiting(locationId, subLocationId)
            .flowOn(Dispatchers.IO)
            .singleOrNull() ?: throw NullPointerException()
        val listWaitingQueue = ArrayList<Queue>()

        response.waitingList.forEach { item ->
            listWaitingQueue.add(
                Queue(
                    id = item.id,
                    number = item.number,
                    createdAt = item.createdAt,
                    message = item.message,
                    currentQueue = item.currentQueue,
                    totalQueue = item.totalQueue,
                    timeOrder = item.timeOrder,
                    subLocationId = item.subLocationId
                )
            )
        }
        val result = ListQueueResult(response.count, listWaitingQueue)
        emit(ListQueueWaitingState.Success(result))
    }
}