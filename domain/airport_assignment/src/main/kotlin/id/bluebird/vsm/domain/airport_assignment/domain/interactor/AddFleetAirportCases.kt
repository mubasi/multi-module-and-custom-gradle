package id.bluebird.vsm.domain.airport_assignment.domain.interactor

import id.bluebird.vsm.domain.airport_assignment.AirportAssignmentRepository
import id.bluebird.vsm.domain.airport_assignment.StockDepartState
import id.bluebird.vsm.domain.airport_assignment.domain.cases.AddFleetAirport
import id.bluebird.vsm.domain.airport_assignment.model.AddStockDepartModel
import id.bluebird.vsm.domain.airport_assignment.model.ArrivedItemModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class AddFleetAirportCases(
    private val airportAssignmentRepository: AirportAssignmentRepository
) : AddFleetAirport {
    override fun invoke(
        locationId: Long,
        fleetNumber: String,
        subLocation: Long,
        isTu: Boolean
    ): Flow<StockDepartState> = flow {
        val response = airportAssignmentRepository.addFleetAirport(
            locationId, fleetNumber, subLocation, isTu
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
            currentTuSpace = response.currentTuSpace,
            arrivedItem = tempArrivedItem,
            stockType = response.stockType
        )

        emit(StockDepartState.Success(result))
    }
}