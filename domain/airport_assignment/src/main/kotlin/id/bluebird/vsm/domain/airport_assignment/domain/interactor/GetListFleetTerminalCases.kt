package id.bluebird.vsm.domain.airport_assignment.domain.interactor

import id.bluebird.vsm.core.extensions.StringExtensions.convertCreateAtValue
import id.bluebird.vsm.domain.airport_assignment.AirportAssignmentRepository
import id.bluebird.vsm.domain.airport_assignment.GetListFleetTerminalDepartState
import id.bluebird.vsm.domain.airport_assignment.domain.cases.GetListFleetTerminal
import id.bluebird.vsm.domain.airport_assignment.model.FleetItemDepartModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class GetListFleetTerminalCases(
    private val airportAssignmentRepository: AirportAssignmentRepository
) : GetListFleetTerminal {
    override fun invoke(
        subLocationId: Long,
        page: Int,
        itemPerPage: Int
    ): Flow<GetListFleetTerminalDepartState> = flow {
        val response = airportAssignmentRepository.getListFleetTerminalDepart(
            subLocationId, page, itemPerPage
        )
            .flowOn(Dispatchers.IO)
            .singleOrNull() ?: throw NullPointerException()

        if(response.fleetListList.isEmpty()) {
            emit(
                GetListFleetTerminalDepartState.EmptyResult
            )
        } else {
            val result: ArrayList<FleetItemDepartModel> = ArrayList()
            response.fleetListList.map {
                result.add(
                    FleetItemDepartModel(
                        fleetId = it.fleetId,
                        taxiNo = it.taxiNo,
                        createdAt = it.createdAt.convertCreateAtValue(),
                        status = it.status,
                        isTu = false
                    )
                )
            }
            emit(GetListFleetTerminalDepartState.Success(result))
        }
    }
}