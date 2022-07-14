package id.bluebird.mall.domain_pasenger.domain.cases

import id.bluebird.mall.domain_pasenger.TakeQueueState
import kotlinx.coroutines.flow.Flow

interface TakeQueue {
    operator fun invoke(
        queueId: Long,
        queueType: Long,
        locationId :  Long,
        queueNumber: String,
        subLocationId: Long,
        fleetNumber: String
    ) : Flow<TakeQueueState>
}