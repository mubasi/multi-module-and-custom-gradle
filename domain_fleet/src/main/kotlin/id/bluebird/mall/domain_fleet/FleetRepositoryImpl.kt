package id.bluebird.mall.domain_fleet

import com.google.protobuf.Timestamp
import id.bluebird.mall.core.utils.OkHttpChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import proto.AssignmentPangkalanGrpc
import proto.AssignmentPangkalanOuterClass

class FleetRepositoryImpl(
    private val assignmentGrpc: AssignmentPangkalanGrpc.AssignmentPangkalanBlockingStub = AssignmentPangkalanGrpc.newBlockingStub(
        OkHttpChannel.channel
    )
) : FleetRepository {

    override fun getCount(
        subLocation: Long,
        locationId: Long,
        todayEpoch: Long
    ): Flow<AssignmentPangkalanOuterClass.StockCountResponse> =
        flow {
            val request = AssignmentPangkalanOuterClass.StockCountRequest.newBuilder()
                .apply {
                    subLocationId = subLocation
                    this.locationId = locationId
                    this.createdAt = Timestamp.newBuilder().setSeconds(todayEpoch).build()
                }.build()
            val result = assignmentGrpc.getSubLocationStockCount(request)
            emit(result)
        }

    override fun searchFleet(param: String): Flow<AssignmentPangkalanOuterClass.SearchStockRequest> =
        flow {
            val request = AssignmentPangkalanOuterClass.SearchStockRequest.newBuilder()
                .apply {
                    this.fleetNumber = param
                }
        }
}