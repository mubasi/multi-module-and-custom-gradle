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

    fun getCurrentQueue (
        locationId: Long
    ) : Flow<QueuePangkalanOuterClass.GetCurrentQueueResponse>

    fun skipQueue (
        queueId: Long,
        locationId: Long,
        subLocationId: Long,
    ) : Flow<QueuePangkalanOuterClass.ResponseSkipCurrentQueue>

    fun listQueueWaiting (
        locationId: Long,
    ) : Flow<QueuePangkalanOuterClass.ResponseGetWaitingQueue>

    fun listQueueSkipped (
        locationId: Long,
    ) : Flow<QueuePangkalanOuterClass.ResponseGetSkippedQueue>

    fun getWaitingQueue(locationId: Long): Flow<QueuePangkalanOuterClass.ResponseGetWaitingQueue>

    fun searchWaitingQueue(
        queueNumber: String,
        locationId: Long,
        subLocationId: Long
    ): Flow<QueuePangkalanOuterClass.ResponseSearchQueue>

    fun deleteSkippedQueue (
        queueId: Long,
        queueType: Long,
        locationId: Long,
        queueNumber: String,
        subLocationId: Long,
        fleetNumber: String,
    ) : Flow<QueuePangkalanOuterClass.ResponseQueues>


    fun restoreSkippedQueue (
        queueId: Long,
        queueType: Long,
        locationId: Long,
        queueNumber: String,
        subLocationId: Long,
        fleetNumber: String,
    ) : Flow<QueuePangkalanOuterClass.ResponseQueues>

    fun counterBar (
        locationId: Long,
    ) : Flow<QueuePangkalanOuterClass.responseGetCountQueue>

    fun searchQueue (
        queueNumber: String,
        locationId: Long,
        subLocationId: Long,
        typeQueue: QueuePangkalanOuterClass.QueueType
    ) : Flow<QueuePangkalanOuterClass.ResponseSearchQueue>
}