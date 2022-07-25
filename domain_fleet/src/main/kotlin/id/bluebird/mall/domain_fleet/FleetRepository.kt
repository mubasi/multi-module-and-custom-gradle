package id.bluebird.mall.domain_fleet

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
}