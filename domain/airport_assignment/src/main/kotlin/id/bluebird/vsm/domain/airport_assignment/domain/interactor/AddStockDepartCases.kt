package id.bluebird.vsm.domain.airport_assignment.domain.interactor

import id.bluebird.vsm.domain.airport_assignment.AirportAssignmentRepository
import id.bluebird.vsm.domain.airport_assignment.StockDepartState
import id.bluebird.vsm.domain.airport_assignment.domain.cases.AddStockDepart
import id.bluebird.vsm.domain.airport_assignment.model.AddStockDepartModel
import id.bluebird.vsm.domain.airport_assignment.model.ArrivedItemModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class AddStockDepartCases(
    private val airportAssignment: AirportAssignmentRepository
) : AddStockDepart {
    override fun invoke(
        locationId: Long,
        subLocationId: Long,
        taxiNo: String,
        isWithPassenger: Long,
        isArrived: Boolean,
        queueNumber: String,
        departFleetItem: List<Long>
    ): Flow<StockDepartState> = flow {
        val response = airportAssignment.stockDepart(
            locationId, subLocationId, taxiNo, isWithPassenger, isArrived, queueNumber, departFleetItem
        )
            .flowOn(Dispatchers.IO)
            .singleOrNull() ?: throw NullPointerException()

        val itemTaxi : ArrayList<String> = ArrayList()
        response.taxiNoList.map {
            itemTaxi.add(it)
        }

        val arrivedList : ArrayList<ArrivedItemModel> = ArrayList()
        response.arrivedFleetList.map {
            arrivedList.add(
                ArrivedItemModel(
                    stockId = it.stockId,
                    taxiNo = it.taxiNo,
                    createdAt = it.createdAt
                )
            )
        }

        val result = AddStockDepartModel(
            massage = response.message,
            stockId = response.stockId,
            stockType = response.stockType,
            createdAt = response.createdAt,
            taxiList = itemTaxi,
            arrivedItem = arrivedList,
            currentTuSpace = response.currentTuSpace
        )

        emit(StockDepartState.Success(result))
    }
}