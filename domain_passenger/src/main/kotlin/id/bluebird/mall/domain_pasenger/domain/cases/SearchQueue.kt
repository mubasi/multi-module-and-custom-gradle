package id.bluebird.mall.domain_pasenger.domain.cases

import id.bluebird.mall.domain_pasenger.SearchQueueState
import kotlinx.coroutines.flow.Flow
import proto.QueuePangkalanOuterClass

interface SearchQueue {
    operator fun invoke(
        queueNumber: String,
        locationId :  Long,
        subLocationId: Long,
        typeQueue: QueuePangkalanOuterClass.QueueType
    ) : Flow<SearchQueueState>
}