package id.bluebird.vsm.domain.passenger.domain.cases

import id.bluebird.vsm.domain.passenger.GetQueueReceiptState
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