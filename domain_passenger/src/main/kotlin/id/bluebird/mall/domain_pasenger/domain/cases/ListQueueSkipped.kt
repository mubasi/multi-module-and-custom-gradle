package id.bluebird.mall.domain_pasenger.domain.cases

import id.bluebird.mall.domain_pasenger.ListQueueSkippedState
import kotlinx.coroutines.flow.Flow

interface ListQueueSkipped {
    operator fun invoke(
        locationId :  Long,
    ) : Flow<ListQueueSkippedState>
}