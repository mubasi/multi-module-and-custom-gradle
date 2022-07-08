package id.bluebird.mall.domain_fleet

import kotlinx.coroutines.flow.Flow
import proto.AssignmentPangkalanOuterClass

interface FleetRepository {
    fun getCount(
        subLocation: Long,
        locationId: Long,
        todayEpoch: Long
    ): Flow<AssignmentPangkalanOuterClass.StockCountResponse>

    fun searchFleet(param: String): Flow<AssignmentPangkalanOuterClass.SearchStockRequest>
}