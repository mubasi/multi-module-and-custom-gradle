package id.bluebird.vsm.domain.passenger.domain.cases

import id.bluebird.vsm.domain.passenger.SearchQueueState
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