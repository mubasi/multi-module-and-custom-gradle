package id.bluebird.mall.domain_fleet

import com.google.protobuf.Timestamp
import id.bluebird.mall.core.utils.OkHttpChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import proto.AssignmentPangkalanGrpc
import proto.AssignmentPangkalanOuterClass
import proto.FleetOuterClass
import proto.FleetServiceGrpc
import proto.FleetServiceGrpc.newBlockingStub

class FleetRepositoryImpl(
    private val assignmentGrpc: AssignmentPangkalanGrpc.AssignmentPangkalanBlockingStub = AssignmentPangkalanGrpc.newBlockingStub(
        OkHttpChannel.channel
    ),
    private val fleetGrpc: FleetServiceGrpc.FleetServiceBlockingStub = newBlockingStub(
        OkHttpChannel.channel
    )
) : FleetRepository {

    companion object {
        private const val DEFAULT = 0L
    }

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

    override fun searchFleet(
        param: String,
        itemPerPage: Int
    ): Flow<FleetOuterClass.SearchResponse> =
        flow {
            val request = FleetOuterClass.SearchRequest.newBuilder().apply {
                keyword = param
                paging = FleetOuterClass.PagingRequest.newBuilder().apply {
                    this.itemPerPage = itemPerPage
                    page = 1
                }
                    .build()
            }
                .build()
            val result = fleetGrpc.search(request)
            emit(result)
        }

    override fun requestFleet(
        count: Long,
        locationId: Long,
        subLocation: Long
    ): Flow<AssignmentPangkalanOuterClass.RequestTaxiResponse> =
        flow {
            val request = AssignmentPangkalanOuterClass.RequestTaxiRequest.newBuilder()
                .apply {
                    this.count = count
                    this.locationId = locationId
                    requestFrom = subLocation
                }
                .build()
            val result = assignmentGrpc.requestTaxi(request)
            emit(result)
        }

    override fun addFleet(
        fleetNumber: String,
        subLocationId: Long,
        locationId: Long
    ): Flow<AssignmentPangkalanOuterClass.StockResponse> = flow {
        val request = AssignmentPangkalanOuterClass.StockRequest.newBuilder()
            .apply {
                taxiNo = fleetNumber
                isArrived = false
                stockType = AssignmentPangkalanOuterClass.StockType.IN
                isWithPassenger = DEFAULT
                this.subLocationId = subLocationId
                this.locationId = locationId
            }.build()
        val result = assignmentGrpc.stock(request)
        emit(result)
    }
}