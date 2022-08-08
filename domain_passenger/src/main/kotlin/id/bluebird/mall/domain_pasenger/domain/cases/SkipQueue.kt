package id.bluebird.mall.domain_pasenger.domain.cases

import id.bluebird.mall.domain_pasenger.SkipQueueState
import kotlinx.coroutines.flow.Flow

interface SkipQueue {
    operator fun invoke(
        queueId :  Long,
        locationId :  Long,
        subLocationId :  Long
    ) : Flow<SkipQueueState>
}