package id.bluebird.mall.domain_pasenger

import id.bluebird.mall.core.utils.OkHttpChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import proto.QueuePangkalanGrpc
import proto.QueuePangkalanOuterClass

class QueueReceiptRepositoryimpl(
    private val queuePangkalanGrpc: QueuePangkalanGrpc.QueuePangkalanBlockingStub = QueuePangkalanGrpc.newBlockingStub(
        OkHttpChannel.channel
    )
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
}