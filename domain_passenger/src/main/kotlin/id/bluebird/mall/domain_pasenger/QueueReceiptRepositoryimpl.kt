package id.bluebird.mall.domain_pasenger

import id.bluebird.mall.core.utils.OkHttpChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import proto.QueuePangkalanGrpc
import proto.QueuePangkalanOuterClass

class QueueReceiptRepositoryimpl(
    private val queuePangkalanGrpc: QueuePangkalanGrpc.QueuePangkalanBlockingStub = QueuePangkalanGrpc.newBlockingStub(
        OkHttpChannel.channel
    ),
) : QueueReceiptRepository {
    override fun getQueue(
        queueId: Long,
        queueType: Long,
        locationId: Long,
        queueNumber: String,
        subLocationId: Long,
        fleetNumber: String,
    ): Flow<QueuePangkalanOuterClass.ResponseQueues> =
        flow {
            val request = QueuePangkalanOuterClass.RequestQueues.newBuilder()
                .apply {
                    this.queueId = queueId
                    this.queueType = QueuePangkalanOuterClass.QueueType.GENERATE_QUEUE
                    this.locationId = locationId
                    this.queueNumber = queueNumber
                    this.subLocationId = subLocationId
                    this.fleetNumber = fleetNumber
                }.build()

            val result = queuePangkalanGrpc.queues(request)
            emit(result)
        }

    override fun takeQueue(
        queueId: Long,
        queueType: Long,
        locationId: Long,
        queueNumber: String,
        subLocationId: Long,
        fleetNumber: String,
    ): Flow<QueuePangkalanOuterClass.ResponseQueues> =
        flow {
            val request = QueuePangkalanOuterClass.RequestQueues.newBuilder()
                .apply {
                    this.queueId = queueId
                    this.queueType = QueuePangkalanOuterClass.QueueType.TAKE_QUEUE
                    this.locationId = locationId
                    this.queueNumber = queueNumber
                    this.subLocationId = subLocationId
                    this.fleetNumber = fleetNumber
                }.build()

            val result = queuePangkalanGrpc.queues(request)
            emit(result)
        }

    override fun getCurrentQueue(locationId: Long):
            Flow<QueuePangkalanOuterClass.GetCurrentQueueResponse> =
        flow {
            val request = QueuePangkalanOuterClass.GetCurrentQueueRequest.newBuilder()
                .apply {
                    this.locationId = locationId
                }.build()
            val result = queuePangkalanGrpc.getCurrentQueue(request)
            emit(result)
        }

    override fun skipQueue(
        queueId: Long,
        locationId: Long,
        subLocationId: Long,
    ): Flow<QueuePangkalanOuterClass.ResponseSkipCurrentQueue> =
        flow {
            val request = QueuePangkalanOuterClass.RequestSkipCurrentQueue.newBuilder()
                .apply {
                    this.queueId = queueId
                    this.locationId = locationId
                    this.subLocationId = subLocationId
                }.build()

            val result = queuePangkalanGrpc.skipCurrentQueue(request)
            emit(result)
        }

    override fun listQueueWaiting(locationId: Long):
            Flow<QueuePangkalanOuterClass.ResponseGetWaitingQueue> = flow {
        val request = QueuePangkalanOuterClass.RequestGetWaitingQueue.newBuilder()
            .apply {
                this.locationId = locationId
            }.build()
        val result = queuePangkalanGrpc.getWaitingQueue(request)
        emit(result)
    }

    override fun getWaitingQueue(locationId: Long): Flow<QueuePangkalanOuterClass.ResponseGetWaitingQueue> =
        flow {
            val request = QueuePangkalanOuterClass.RequestGetWaitingQueue.newBuilder()
                .apply {
                    this.locationId = locationId
                }.build()

            val result = queuePangkalanGrpc.getWaitingQueue(request)
            emit(result)
        }

    override fun searchWaitingQueue(
        queueNumber: String,
        locationId: Long,
        subLocationId: Long
    ): Flow<QueuePangkalanOuterClass.ResponseSearchQueue> =
        flow {
            val request = QueuePangkalanOuterClass.RequestSearchQueue.newBuilder()
                .apply {
                    this.queueNumber = queueNumber
                    this.locationId = locationId
                    this.subLocationId = subLocationId
                    this.queueType = QueuePangkalanOuterClass.QueueType.SEARCH_WAITING_QUEUE
                }
                .build()

            val result = queuePangkalanGrpc.searchQueue(request)
            emit(result)
        }
}