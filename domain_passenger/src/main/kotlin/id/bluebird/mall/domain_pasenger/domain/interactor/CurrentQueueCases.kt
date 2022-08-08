package id.bluebird.mall.domain_pasenger.domain.interactor

import id.bluebird.mall.domain_pasenger.GetCurrentQueueState
import id.bluebird.mall.domain_pasenger.QueueReceiptRepository
import id.bluebird.mall.domain_pasenger.domain.cases.CurrentQueue
import id.bluebird.mall.domain_pasenger.model.CurrentQueueResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class CurrentQueueCases (private val queueReceiptRepository: QueueReceiptRepository) :
    CurrentQueue {
    override fun invoke(
        locationId: Long
    ): Flow<GetCurrentQueueState> =
        flow {
            val response = queueReceiptRepository.getCurrentQueue(
                locationId
            )
                .flowOn(Dispatchers.IO)
                .singleOrNull() ?: throw NullPointerException()

            val queueCurrentResult = CurrentQueueResult(response.id, response.number, response.createdAt)
            emit(GetCurrentQueueState.Success(queueCurrentResult))
        }
    }