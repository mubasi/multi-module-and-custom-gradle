package id.bluebird.vsm.domain.airport_assignment.domain.interactor

import id.bluebird.vsm.domain.airport_assignment.AirportAssignmentRepository
import id.bluebird.vsm.domain.airport_assignment.RitaseFleetTerminalAirportState
import id.bluebird.vsm.domain.airport_assignment.domain.cases.RitaseFleetTerminalAirport
import id.bluebird.vsm.domain.airport_assignment.model.AddStockDepartModel
import id.bluebird.vsm.domain.airport_assignment.model.ArrivedItemModel
import id.bluebird.vsm.domain.airport_assignment.model.AssignFleetModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class RitaseFleetTerminalAirportCases(
    private val airportAssignmentRepository: AirportAssignmentRepository
) : RitaseFleetTerminalAirport {
    override fun invoke(assignFleetModel: AssignFleetModel): Flow<RitaseFleetTerminalAirportState> = flow {
        val response = airportAssignmentRepository.ritaseFleetTerminalAirport(
            assignFleetModel
        )
            .flowOn(Dispatchers.IO)
            .singleOrNull() ?: throw NullPointerException()

        val tempListTaxiNo = ArrayList<String>()

        response.taxiNoList.forEach {
            tempListTaxiNo.add(
                it
            )
        }

        val tempArrivedItem = ArrayList<ArrivedItemModel>()

        response.arrivedFleetList.forEach {
            tempArrivedItem.add(
                ArrivedItemModel(
                    stockId = it.stockId,
                    createdAt = it.createdAt,
                    taxiNo = it.taxiNo
                )
            )
        }

        val result = AddStockDepartModel(
            massage = response.message,
            taxiList = tempListTaxiNo,
            createdAt = response.createdAt,
            stockId = response.stockId,
            arrivedItem = tempArrivedItem,
            currentTuSpace = response.currentTuSpace,
            stockType = response.stockType
        )

        emit(RitaseFleetTerminalAirportState.Success(result))
    }
}