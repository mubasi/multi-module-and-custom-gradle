package id.bluebird.vsm.domain.airport_assignment.domain.cases

import id.bluebird.vsm.domain.airport_assignment.StockDepartState
import kotlinx.coroutines.flow.Flow

interface AddFleetAirport {
    operator fun invoke(
        locationId : Long,
        fleetNumber: String,
        subLocation: Long,
        isTu: Boolean,
    ) : Flow<StockDepartState>
}