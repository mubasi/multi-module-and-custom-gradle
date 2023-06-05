package id.bluebird.vsm.domain.fleet

import com.google.protobuf.Timestamp
import id.bluebird.vsm.core.utils.OkHttpChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import proto.FleetOuterClass
import proto.FleetServiceGrpc
import proto.OutltetAssignmentPangkalanGrpc
import proto.FleetServiceGrpc.newBlockingStub
import proto.OutletAssignmentPangkalan

class FleetRepositoryImpl(
    private val assignmentGrpc: OutltetAssignmentPangkalanGrpc.OutltetAssignmentPangkalanBlockingStub = OutltetAssignmentPangkalanGrpc.newBlockingStub(
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
        private const val FLEET_TYPE = "REGULER"
    }

    override fun getCount(
        subLocation: Long,
        locationId: Long,
        todayEpoch: Long
    ): Flow<OutletAssignmentPangkalan.StockCountPangkalanResponse> =
        flow {
            val request = OutletAssignmentPangkalan.StockCountPangkalanRequest.newBuilder()
                .apply {
                    subLocationId = subLocation
                    this.locationId = locationId
                    this.createdAt = Timestamp.newBuilder().setSeconds(todayEpoch).build()
                }.build()
            val result = assignmentGrpc.getSubLocationStockCountPangkalan(request)
            emit(result)
        }

    override fun searchFleet(
        param: String,
        itemPerPage: Int
    ): Flow<FleetOuterClass.SearchResponse> =
        flow {
            val request = FleetOuterClass.SearchRequest.newBuilder().apply {
                keyword = param
                fleetType = FLEET_TYPE
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
    ): Flow<OutletAssignmentPangkalan.RequestTaxiPangkalanResponse> =
        flow {
            val request = OutletAssignmentPangkalan.RequestTaxiPangkalanRequest.newBuilder()
                .apply {
                    this.count = count
                    this.locationId = locationId
                    requestFrom = subLocation
                }
                .build()
            val result = assignmentGrpc.requestTaxiPangkalan(request)
            emit(result)
        }

    override fun addFleet(
        fleetNumber: String,
        subLocationId: Long,
        locationId: Long
    ): Flow<OutletAssignmentPangkalan.StockResponsePangkalan> = flow {
        val request = OutletAssignmentPangkalan.StockRequestPangkalan.newBuilder()
            .apply {
                taxiNo = fleetNumber
                isArrived = false
                stockType = OutletAssignmentPangkalan.StockTypePangkalan.STOCK_PANGKALAN_IN
                isWithPassenger = DEFAULT
                this.subLocationId = subLocationId
                this.locationId = locationId
            }.build()
        val result = assignmentGrpc.stockPangkalan(request)
        emit(result)
    }

    override fun getListFleet(subLocationId: Long): Flow<OutletAssignmentPangkalan.GetListFleetTerminalPangkalanResp> =
        flow {
            val request = OutletAssignmentPangkalan.GetListFleetTerminalPangkalanReq.newBuilder()
                .apply {
                    this.subLocation = subLocationId
                    page = PAGE
                    itemPerPage = ITEM_PER_PAGE
                }
                .build()
            val result = assignmentGrpc.getListFleetTerminalPangkalan(request)
            emit(result)
        }

    override fun departFleet(
        locationId: Long,
        subLocationId: Long,
        fleetNumber: String,
        isWithPassenger: Boolean,
        departFleetItems: List<Long>,
        queueNumber: String
    ): Flow<OutletAssignmentPangkalan.StockResponsePangkalan> =
        flow {
            val departItems = departFleetItems.map {
                OutletAssignmentPangkalan.DepartFleetItemsPangkalan.newBuilder().apply { stockId = it }.build()
            }
            val request = OutletAssignmentPangkalan.StockRequestPangkalan.newBuilder()
                .apply {
                    this.locationId = locationId
                    this.subLocationId = subLocationId
                    this.taxiNo = fleetNumber
                    this.isWithPassenger = if (isWithPassenger) 1L else 0L
                    addAllDepartFleetItems(departItems)
                    this.stockType = OutletAssignmentPangkalan.StockTypePangkalan.STOCK_PANGKALAN_OUT
                    this.isArrived = true
                    this.queueNumber = queueNumber
                }
                .build()
            val result = assignmentGrpc.stockPangkalan(request)
            emit(result)
        }
}