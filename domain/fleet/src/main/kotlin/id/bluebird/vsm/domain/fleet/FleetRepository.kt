package id.bluebird.vsm.domain.fleet

import kotlinx.coroutines.flow.Flow
import proto.OutletAssignmentPangkalan
import proto.FleetOuterClass

interface FleetRepository {
    fun getCount(
        subLocation: Long,
        locationId: Long,
        todayEpoch: Long
    ): Flow<OutletAssignmentPangkalan.StockCountPangkalanResponse>

    fun searchFleet(param: String, itemPerPage: Int): Flow<FleetOuterClass.SearchResponse>

    fun requestFleet(
        count: Long,
        locationId: Long,
        subLocation: Long
    ): Flow<OutletAssignmentPangkalan.RequestTaxiPangkalanResponse>

    fun addFleet(
        fleetNumber: String,
        subLocationId: Long,
        locationId: Long
    ): Flow<OutletAssignmentPangkalan.StockResponsePangkalan>

    fun getListFleet(subLocationId: Long): Flow<OutletAssignmentPangkalan.GetListFleetTerminalPangkalanResp>

    fun departFleet(
        locationId: Long,
        subLocationId: Long,
        fleetNumber: String,
        isWithPassenger: Boolean,
        departFleetItems: List<Long>,
        queueNumber: String
    ): Flow<OutletAssignmentPangkalan.StockResponsePangkalan>
}