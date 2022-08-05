package id.bluebird.mall.domain_pasenger.domain.interactor

import id.bluebird.mall.core.utils.hawk.UserUtils
import id.bluebird.mall.domain_pasenger.QueueReceiptRepository
import id.bluebird.mall.domain_pasenger.WaitingQueueState
import id.bluebird.mall.domain_pasenger.domain.cases.SearchWaitingQueue
import id.bluebird.mall.domain_pasenger.model.Queue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class SearchWaitingQueueCases(private val queueReceiptRepository: QueueReceiptRepository): SearchWaitingQueue {
    override fun invoke(
        queueNumber: String,
        subLocationId: Long
    ): Flow<WaitingQueueState<List<Queue>>> = flow {
        val response = queueReceiptRepository
            .searchWaitingQueue(queueNumber, UserUtils.getLocationId(), subLocationId)
            .flowOn(Dispatchers.IO)
            .singleOrNull() ?: throw NullPointerException()

        if (response.queuesCount < 1) {
            emit(WaitingQueueState.EmptyResult)
        } else {
            val result = response.queuesList.map {
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