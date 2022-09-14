package id.bluebird.vsm.domain.passenger.domain.interactor

import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.passenger.GetCurrentQueueState
import id.bluebird.vsm.domain.passenger.QueueReceiptRepository
import id.bluebird.vsm.domain.passenger.domain.cases.GetCurrentQueue
import id.bluebird.vsm.domain.passenger.model.CurrentQueueResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class GetCurrentQueueCase(private val queueReceiptRepository: QueueReceiptRepository): GetCurrentQueue {
    override fun invoke(): Flow<GetCurrentQueueState> =
        flow {
            val response = queueReceiptRepository.getCurrentQueue(UserUtils.getLocationId())
                .flowOn(Dispatchers.IO)
                .singleOrNull() ?: throw NullPointerException()

            val result = CurrentQueueResult(
                id = response.id,
                number = response.number,
                createdAt = response.createdAt
            )

            emit(GetCurrentQueueState.Success(result))

        }
}