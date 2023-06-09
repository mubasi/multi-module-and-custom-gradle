package id.bluebird.vsm.domain.airport_assignment.domain.cases

import id.bluebird.vsm.domain.airport_assignment.RitaseFleetTerminalAirportState
import id.bluebird.vsm.domain.airport_assignment.model.AssignFleetModel
import kotlinx.coroutines.flow.Flow

interface RitaseFleetTerminalAirport {
    operator fun invoke(
        assignFleetModel: AssignFleetModel
    ) : Flow<RitaseFleetTerminalAirportState>
}