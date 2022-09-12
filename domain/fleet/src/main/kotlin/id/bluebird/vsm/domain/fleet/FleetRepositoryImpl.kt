package id.bluebird.vsm.domain.fleet

import com.google.protobuf.Timestamp
import id.bluebird.vsm.core.utils.OkHttpChannel
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
        private const val ITEM_PER_PAGE = 500
        private const val PAGE = 1
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

    override fun getListFleet(subLocationId: Long): Flow<AssignmentPangkalanOuterClass.GetListFleetTerminalResp> =
        flow {
            val request = AssignmentPangkalanOuterClass.GetListFleetTerminalReq.newBuilder()
                .apply {
                    this.subLocation = subLocationId
                    page = PAGE
                    itemPerPage = ITEM_PER_PAGE
                }
                .build()
            val result = assignmentGrpc.getListFleetTerminal(request)
            emit(result)
        }

    override fun departFleet(
        locationId: Long,
        subLocationId: Long,
        fleetNumber: String,
        isWithPassenger: Boolean,
        departFleetItems: List<Long>,
        queueNumber: String
    ): Flow<AssignmentPangkalanOuterClass.StockResponse> =
        flow {
            val departItems = departFleetItems.map {
                AssignmentPangkalanOuterClass.DepartFleetItems.newBuilder().apply { stockId = it }.build()
            }
            val request = AssignmentPangkalanOuterClass.StockRequest.newBuilder()
                .apply {
                    this.locationId = locationId
                    this.subLocationId = subLocationId
                    this.taxiNo = fleetNumber
                    this.isWithPassenger = if (isWithPassenger) 1L else 0L
                    addAllDepartFleetItems(departItems)
                    this.stockType = AssignmentPangkalanOuterClass.StockType.OUT
                    this.isArrived = true
                    this.queueNumber = queueNumber
                }
                .build()
            val result = assignmentGrpc.stock(request)
            emit(result)
        }
}