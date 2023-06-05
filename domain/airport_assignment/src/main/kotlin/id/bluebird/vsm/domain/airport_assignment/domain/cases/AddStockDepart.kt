package id.bluebird.vsm.domain.airport_assignment.domain.cases

import id.bluebird.vsm.domain.airport_assignment.StockDepartState
import kotlinx.coroutines.flow.Flow

interface AddStockDepart {
    operator fun invoke(
        locationId: Long,
        subLocationId: Long,
        taxiNo: String,
        isWithPassenger: Long,
        isArrived: Boolean,
        queueNumber: String,
        departFleetItem: List<Long>
    ) : Flow<StockDepartState>
}