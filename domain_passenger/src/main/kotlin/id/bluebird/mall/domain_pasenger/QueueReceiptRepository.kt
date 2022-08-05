package id.bluebird.mall.domain_pasenger

import proto.QueuePangkalanOuterClass
import kotlinx.coroutines.flow.Flow

interface QueueReceiptRepository {

    fun getQueue (
        queueId: Long,
        queueType: Long,
        locationId: Long,
        queueNumber: String,
        subLocationId: Long,
        fleetNumber: String,
    ) : Flow<QueuePangkalanOuterClass.ResponseQueues>


    fun takeQueue (
        queueId: Long,
        queueType: Long,
        locationId: Long,
        queueNumber: String,
        subLocationId: Long,
        fleetNumber: String,
    ) : Flow<QueuePangkalanOuterClass.ResponseQueues>

    fun getWaitingQueue(locationId: Long): Flow<QueuePangkalanOuterClass.ResponseGetWaitingQueue>

    fun getCurrentQueue(locationId: Long): Flow<QueuePangkalanOuterClass.GetCurrentQueueResponse>

    fun searchWaitingQueue(queueNumber: String, locationId: Long, subLocationId: Long): Flow<QueuePangkalanOuterClass.ResponseSearchQueue>
}