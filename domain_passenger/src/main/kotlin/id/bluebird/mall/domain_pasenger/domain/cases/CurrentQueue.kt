package id.bluebird.mall.domain_pasenger.domain.cases

import id.bluebird.mall.domain_pasenger.GetCurrentQueueState
import kotlinx.coroutines.flow.Flow

interface CurrentQueue {
    operator fun invoke(
        locationId :  Long,
    ) : Flow<GetCurrentQueueState>
}