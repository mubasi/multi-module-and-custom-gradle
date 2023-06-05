package id.bluebird.vsm.domain.airport_assignment

import id.bluebird.vsm.core.utils.OkHttpChannel
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.airport_assignment.model.AssignFleetModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import proto.AssignmentGrpc
import proto.AssignmentOuterClass


class AirportAssignmentRepositoryImpl(
    private val outletAssignGrpc: AssignmentGrpc.AssignmentBlockingStub = AssignmentGrpc.newBlockingStub(
        OkHttpChannel.channel
    )
) : AirportAssignmentRepository {

    override fun stockDepart(
        locationId: Long,
        subLocationId: Long,
        taxiNo: String,
        isWithPassenger: Long,
        isArrived: Boolean,
        queueNumber: String,
        departFleetItem: List<Long>
    ): Flow<AssignmentOuterClass.StockResponse> = flow {
        val departItem = departFleetItem.map {
            AssignmentOuterClass.DepartFleetItems.newBuilder()
                .apply {
                    stockId = it
                }.build()
        }

        val request = AssignmentOuterClass.StockRequest.newBuilder()
            .apply {
                this.subLocationId = subLocationId
                this.taxiNo = taxiNo
                this.isWithPassenger = isWithPassenger
                this.locationId = locationId
                this.isArrived = isArrived
                this.stockType = AssignmentOuterClass.StockType.IN
                addAllDepartFleetItems(departItem)
            }.build()
        val result = outletAssignGrpc.stock(request)
        emit(result)
    }

    override fun requestTaxiDepart(
        requestFrom: Long,
        locationId: Long,
        count: Long
    ): Flow<AssignmentOuterClass.RequestTaxiResponse> = flow {
        val request = AssignmentOuterClass.RequestTaxiRequest.newBuilder()
            .apply {
                this.count = count
                this.locationId = locationId
                this.requestFrom = requestFrom
            }
            .build()
        val result = outletAssignGrpc.requestTaxi(request)
        emit(result)
    }

    override fun getSubLocationStockCountDepart(
        subLocationId: Long,
        locationId: Long
    ): Flow<AssignmentOuterClass.StockCountResponse> = flow {
        val request = AssignmentOuterClass.StockCountRequest.newBuilder()
            .apply {
                this.subLocationId = subLocationId
                this.locationId = locationId
            }.build()
        val result = outletAssignGrpc.getSubLocationStockCount(request)
        emit(result)
    }

    override fun getListFleetTerminalDepart(
        subLocationId: Long,
        page: Int,
        itemPerPage: Int
    ): Flow<AssignmentOuterClass.GetListFleetTerminalResp> = flow {
        val request = AssignmentOuterClass.GetListFleetTerminalReq.newBuilder()
            .apply {
                this.subLocation = subLocationId
                this.page = page
                this.itemPerPage = itemPerPage
            }.build()

        val result = outletAssignGrpc.getListFleetTerminal(request)
        emit(result)
    }

    override fun addFleetAirport(
        locationId: Long,
        fleetNumber: String,
        subLocationId: Long,
        isTu: Boolean
    ): Flow<AssignmentOuterClass.StockResponse> = flow {
        val request = AssignmentOuterClass.StockRequest.newBuilder()
            .setStockType(AssignmentOuterClass.StockType.IN)
            .setIsWithPassenger(0)
            .setLocationId(locationId)
            .setSubLocationId(subLocationId)
            .setTaxiNo(fleetNumber)
            .setTu(isTu)
            .build()
        val result = outletAssignGrpc.stock(request)
        emit(result)
    }

    override fun dispatchFleetFromTerminal(
        locationId: Long,
        subLocationId: Long,
        isArrived: Boolean,
        withPassenger: Long,
        stockIdList: List<Long>
    ): Flow<AssignmentOuterClass.StockResponse> = flow {

        val departFleets: MutableList<AssignmentOuterClass.DepartFleetItems> = ArrayList()
        stockIdList.forEach {
            departFleets.add(
                AssignmentOuterClass.DepartFleetItems.newBuilder().setStockId(it)
                    .build()
            )
        }
        val request = AssignmentOuterClass.StockRequest.newBuilder()
            .setStockType(
                if (isArrived) AssignmentOuterClass.StockType.OUT else AssignmentOuterClass.StockType.IN
            )
            .addAllDepartFleetItems(departFleets)
            .setIsWithPassenger(withPassenger)
            .setLocationId(locationId)
            .setTaxiNo("")
            .setSubLocationId(subLocationId)
            .setIsArrived(!isArrived)
            .build()
        val result = outletAssignGrpc.stock(request)
        emit(result)
    }

    override fun getSubLocationAssignmentByLocationId(
        locationId: Long,
        showWingsChild: Boolean,
        versionCode: Long
    ): Flow<AssignmentOuterClass.ResponseSubLocation> = flow {
        val request = AssignmentOuterClass.RequestSublocation.newBuilder()
            .apply {
                this.locationId = locationId
                this.versionCode = versionCode
                this.requestFilter =
                    AssignmentOuterClass.RequestFilter.newBuilder()
                        .setShowWingsChild(showWingsChild)
                        .build()
            }
            .build()
        val result = outletAssignGrpc.getRequestSublocations(request)
        emit(result)
    }

    override fun ritaseFleetTerminalAirport(assignFleetModel: AssignFleetModel): Flow<AssignmentOuterClass.StockResponse> = flow {
        val departFleets: MutableList<AssignmentOuterClass.DepartFleetItems> = ArrayList()
        assignFleetModel.carsAssignment.forEach {
            departFleets.add(
                AssignmentOuterClass.DepartFleetItems.newBuilder().setStockId(it.fleetId)
                    .build()
            )
        }
        val locationId = UserUtils.getLocationId()
        val request = AssignmentOuterClass.StockRequest.newBuilder()
            .setStockType(
                if (assignFleetModel.isArrived) AssignmentOuterClass.StockType.OUT else AssignmentOuterClass.StockType.IN
            )
            .addAllDepartFleetItems(departFleets)
            .setIsWithPassenger(if (assignFleetModel.withPassenger) 1 else 0)
            .setLocationId(locationId)
            .setTaxiNo("")
            .setSubLocationId(assignFleetModel.subLocationId)
            .setIsArrived(!assignFleetModel.isArrived)
            .build()
        val result = outletAssignGrpc.stock(request)
        emit(result)
    }

    override fun assignFleetTerminal(assignFleetModel: AssignFleetModel): Flow<AssignmentOuterClass.AssignFleetResponse> = flow {
        val locationId = UserUtils.getLocationId()
        val assignFleet: MutableList<AssignmentOuterClass.AssignFleetItems> = ArrayList()
        assignFleetModel.carsAssignment.forEach {
            assignFleet.add(
                AssignmentOuterClass.AssignFleetItems.newBuilder()
                    .setStockId(it.fleetId).build()
            )
        }
        val request = AssignmentOuterClass.AssignFleetRequest.newBuilder()
            .addAllAssignFleet(assignFleet)
            .setLocationId(locationId)
            .setAssignLocation(assignFleetModel.subLocationId)
            .build()
        val result = outletAssignGrpc.assignFleetTerminal(request)
        emit(result)
    }

    override fun getDetailRequestInLocation(
        locationId: Long,
        showWingsChild: Boolean
    ): Flow<AssignmentOuterClass.ResponseGetDetailRequest> = flow {
        val request = AssignmentOuterClass.RequestGetDetailRequest.newBuilder().apply {
            this.locationId = locationId
            this.showWingsChild = showWingsChild
        }.build()
        val result = outletAssignGrpc.getDetailRequestInLocation(request)
        emit(result)
    }
}