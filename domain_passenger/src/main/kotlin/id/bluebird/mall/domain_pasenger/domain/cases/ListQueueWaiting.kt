package id.bluebird.mall.domain_pasenger.domain.cases

import id.bluebird.mall.domain_pasenger.ListQueueWaitingState
import kotlinx.coroutines.flow.Flow

interface ListQueueWaiting {
    operator fun invoke(
        locationId :  Long,
    ) : Flow<ListQueueWaitingState>
}