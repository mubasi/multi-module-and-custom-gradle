package id.bluebird.mall.domain_pasenger.domain.interactor

import id.bluebird.mall.core.utils.hawk.UserUtils
import id.bluebird.mall.domain_pasenger.GetCurrentQueueState
import id.bluebird.mall.domain_pasenger.QueueReceiptRepository
import id.bluebird.mall.domain_pasenger.domain.cases.GetCurrentQueue
import id.bluebird.mall.domain_pasenger.model.CurrentQueue
import id.bluebird.mall.domain_pasenger.model.CurrentQueueResult
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