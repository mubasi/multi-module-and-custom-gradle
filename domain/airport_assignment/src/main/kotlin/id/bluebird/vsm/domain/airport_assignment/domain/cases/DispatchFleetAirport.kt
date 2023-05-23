package id.bluebird.vsm.domain.airport_assignment.domain.cases

import id.bluebird.vsm.domain.airport_assignment.DispatchFleetAirportState
import id.bluebird.vsm.domain.airport_assignment.model.DispatchFleetModel
import kotlinx.coroutines.flow.Flow

interface DispatchFleetAirport {
    operator fun invoke(dispatchFleetModel: DispatchFleetModel): Flow<DispatchFleetAirportState>
}