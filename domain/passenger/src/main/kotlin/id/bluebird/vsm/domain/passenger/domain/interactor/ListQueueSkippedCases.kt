package id.bluebird.vsm.domain.passenger.domain.interactor

import id.bluebird.vsm.domain.passenger.ListQueueSkippedState
import id.bluebird.vsm.domain.passenger.QueueReceiptRepository
import id.bluebird.vsm.domain.passenger.domain.cases.ListQueueSkipped
import id.bluebird.vsm.domain.passenger.model.ListQueueResult
import id.bluebird.vsm.domain.passenger.model.Queue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class ListQueueSkippedCases(private val queueReceiptRepository: QueueReceiptRepository) :
    ListQueueSkipped {
    override fun invoke(locationId: Long):
            Flow<ListQueueSkippedState> = flow {
        val response = queueReceiptRepository.listQueueSkipped(locationId)
            .flowOn(Dispatchers.IO)
            .singleOrNull() ?: throw NullPointerException()
        val listSkippedQueue = ArrayList<Queue>()

        response.skippedList.forEach { item ->
            listSkippedQueue.add(
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
        val result = ListQueueResult(response.count, listSkippedQueue)
        emit(ListQueueSkippedState.Success(result))
    }
}