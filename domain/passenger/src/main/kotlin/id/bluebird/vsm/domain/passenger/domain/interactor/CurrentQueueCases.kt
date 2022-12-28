package id.bluebird.vsm.domain.passenger.domain.interactor

import id.bluebird.vsm.domain.passenger.GetCurrentQueueState
import id.bluebird.vsm.domain.passenger.QueueReceiptRepository
import id.bluebird.vsm.domain.passenger.domain.cases.CurrentQueue
import id.bluebird.vsm.domain.passenger.model.CurrentQueueResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class CurrentQueueCases (private val queueReceiptRepository: QueueReceiptRepository) :
    CurrentQueue {
    override fun invoke(
        locationId: Long,
        subLocationId: Long,
    ): Flow<GetCurrentQueueState> =
        flow {
            val response = queueReceiptRepository.getCurrentQueue(
                locationId,
                subLocationId
            )
                .flowOn(Dispatchers.IO)
                .singleOrNull() ?: throw NullPointerException()

            val queueCurrentResult = CurrentQueueResult(response.id, response.number, response.createdAt)
            emit(GetCurrentQueueState.Success(queueCurrentResult))
        }
    }