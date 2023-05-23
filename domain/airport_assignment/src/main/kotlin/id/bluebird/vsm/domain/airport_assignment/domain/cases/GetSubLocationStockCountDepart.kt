package id.bluebird.vsm.domain.airport_assignment.domain.cases

import id.bluebird.vsm.domain.airport_assignment.GetSubLocationStockCountDepartState
import kotlinx.coroutines.flow.Flow

interface GetSubLocationStockCountDepart {
    operator fun invoke(
        subLocationId: Long,
        locationId: Long,
        todayEpoch: Long
    ) : Flow<GetSubLocationStockCountDepartState>
}