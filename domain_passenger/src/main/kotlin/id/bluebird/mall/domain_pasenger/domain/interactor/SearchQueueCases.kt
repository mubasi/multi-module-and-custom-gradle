package id.bluebird.mall.domain_pasenger.domain.interactor

import id.bluebird.mall.domain_pasenger.QueueReceiptRepository
import id.bluebird.mall.domain_pasenger.SearchQueueState
import id.bluebird.mall.domain_pasenger.domain.cases.SearchQueue
import id.bluebird.mall.domain_pasenger.model.Queue
import id.bluebird.mall.domain_pasenger.model.SearchQueueResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull
import proto.QueuePangkalanOuterClass

class SearchQueueCases(private val queueReceiptRepository: QueueReceiptRepository) :
    SearchQueue {
    override fun invoke(
        queueNumber: String,
        locationId: Long,
        subLocationId: Long,
        typeQueue: QueuePangkalanOuterClass.QueueType
    ): Flow<SearchQueueState> =
        flow {
            val response = queueReceiptRepository.searchQueue(
                queueNumber, locationId, subLocationId, typeQueue
            ).flowOn(Dispatchers.IO)
                .singleOrNull() ?: throw NullPointerException()
            val listQueue = ArrayList<Queue>()
            response.queuesList.forEach { item ->
                listQueue.add(
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

            val searchQueue = SearchQueueResult(
                response.searchType, listQueue
            )
            emit(SearchQueueState.Success(searchQueue))
        }
}