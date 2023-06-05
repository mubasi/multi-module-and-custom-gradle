package id.bluebird.vsm.domain.airport_assignment

import id.bluebird.vsm.domain.airport_assignment.model.AssignFleetModel
import kotlinx.coroutines.flow.Flow
import proto.AssignmentOuterClass

interface AirportAssignmentRepository {
    /**
     * StockPangkalan
     */
    fun stockDepart(
        locationId : Long,
        subLocationId : Long,
        taxiNo : String,
        isWithPassenger : Long,
        isArrived : Boolean,
        queueNumber : String,
        departFleetItem : List<Long>,
    ) : Flow<AssignmentOuterClass.StockResponse>

    /**
     * RequestTaxiPangkalan
     */
    fun requestTaxiDepart(
        requestFrom : Long,
        locationId : Long,
        count : Long
    ) : Flow<AssignmentOuterClass.RequestTaxiResponse>

    /**
     * GetSubLocationStockCountPangkalan
     */
    fun getSubLocationStockCountDepart(
        subLocationId : Long,
        locationId : Long
    ) : Flow<AssignmentOuterClass.StockCountResponse>

    /**
     * GetListFleetTerminalPangkalan
     */
    fun getListFleetTerminalDepart(
        subLocationId : Long,
        page : Int,
        itemPerPage : Int
    ) : Flow<AssignmentOuterClass.GetListFleetTerminalResp>

    fun addFleetAirport(
        locationId : Long,
        fleetNumber : String,
        subLocationId: Long,
        isTu : Boolean
    ) : Flow<AssignmentOuterClass.StockResponse>

    fun dispatchFleetFromTerminal(
        locationId: Long,
        subLocationId: Long,
        isArrived: Boolean,
        withPassenger: Long,
        stockIdList: List<Long>
    ): Flow<AssignmentOuterClass.StockResponse>

    fun getSubLocationAssignmentByLocationId(
        locationId: Long, showWingsChild: Boolean = false, versionCode:Long = -1
    ): Flow<AssignmentOuterClass.ResponseSubLocation>

    fun ritaseFleetTerminalAirport(assignFleetModel: AssignFleetModel): Flow<AssignmentOuterClass.StockResponse>

    fun assignFleetTerminal(assignFleetModel: AssignFleetModel): Flow<AssignmentOuterClass.AssignFleetResponse>

    fun getDetailRequestInLocation(
        locationId: Long,
        showWingsChild: Boolean
    ): Flow<AssignmentOuterClass.ResponseGetDetailRequest>
}