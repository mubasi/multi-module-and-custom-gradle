package id.bluebird.mall.domain_pasenger.domain.cases

import id.bluebird.mall.domain_pasenger.GetQueueReceiptState
import kotlinx.coroutines.flow.Flow

interface GetQueueReceipt {
    operator fun invoke(
        queueId: Long,
        queueType: Long,
        locationId :  Long,
        queueNumber: String,
        subLocationId: Long,
        fleetNumber: String
    ) : Flow<GetQueueReceiptState>
}