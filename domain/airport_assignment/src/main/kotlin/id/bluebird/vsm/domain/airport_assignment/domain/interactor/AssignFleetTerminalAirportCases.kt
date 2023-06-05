package id.bluebird.vsm.domain.airport_assignment.domain.interactor

import id.bluebird.vsm.domain.airport_assignment.AirportAssignmentRepository
import id.bluebird.vsm.domain.airport_assignment.AssignFleetTerminalAirportState
import id.bluebird.vsm.domain.airport_assignment.domain.cases.AssignFleetTerminalAirport
import id.bluebird.vsm.domain.airport_assignment.model.AssignFleetModel
import id.bluebird.vsm.domain.airport_assignment.model.AssignFleetTerminalAirportModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class AssignFleetTerminalAirportCases(
    private val airportAssignmentRepository: AirportAssignmentRepository
) : AssignFleetTerminalAirport {
    override fun invoke(assignFleetModel: AssignFleetModel): Flow<AssignFleetTerminalAirportState> = flow {
        val response = airportAssignmentRepository.assignFleetTerminal(
            assignFleetModel
        )
            .flowOn(Dispatchers.IO)
            .singleOrNull() ?: throw NullPointerException()

        val result = AssignFleetTerminalAirportModel (
            message = response.message,
            totalAssignedFleet = response.totalAssignedFleet
        )

        emit(AssignFleetTerminalAirportState.Success(result))
    }
}