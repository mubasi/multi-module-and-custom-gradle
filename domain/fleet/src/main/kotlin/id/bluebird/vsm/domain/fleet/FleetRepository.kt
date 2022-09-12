package id.bluebird.vsm.domain.fleet

import kotlinx.coroutines.flow.Flow
import proto.AssignmentPangkalanOuterClass
import proto.FleetOuterClass

interface FleetRepository {
    fun getCount(
        subLocation: Long,
        locationId: Long,
        todayEpoch: Long
    ): Flow<AssignmentPangkalanOuterClass.StockCountResponse>

    fun searchFleet(param: String, itemPerPage: Int): Flow<FleetOuterClass.SearchResponse>

    fun requestFleet(
        count: Long,
        locationId: Long,
        subLocation: Long
    ): Flow<AssignmentPangkalanOuterClass.RequestTaxiResponse>

    fun addFleet(
        fleetNumber: String,
        subLocationId: Long,
        locationId: Long
    ): Flow<AssignmentPangkalanOuterClass.StockResponse>

    fun getListFleet(subLocationId: Long): Flow<AssignmentPangkalanOuterClass.GetListFleetTerminalResp>

    fun departFleet(
        locationId: Long,
        subLocationId: Long,
        fleetNumber: String,
        isWithPassenger: Boolean,
        departFleetItems: List<Long>,
        queueNumber: String
    ): Flow<AssignmentPangkalanOuterClass.StockResponse>
}