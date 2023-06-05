package id.bluebird.vsm.domain.airport_assignment.domain.cases

import id.bluebird.vsm.domain.airport_assignment.AssignFleetTerminalAirportState
import id.bluebird.vsm.domain.airport_assignment.model.AssignFleetModel
import kotlinx.coroutines.flow.Flow

interface AssignFleetTerminalAirport {
    operator fun invoke(
        assignFleetModel: AssignFleetModel
    ) : Flow<AssignFleetTerminalAirportState>
}