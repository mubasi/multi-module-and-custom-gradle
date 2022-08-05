package id.bluebird.mall.domain_pasenger.domain.cases

import id.bluebird.mall.domain_pasenger.GetCurrentQueueState
import kotlinx.coroutines.flow.Flow

interface GetCurrentQueue {
    operator fun invoke(): Flow<GetCurrentQueueState>
}