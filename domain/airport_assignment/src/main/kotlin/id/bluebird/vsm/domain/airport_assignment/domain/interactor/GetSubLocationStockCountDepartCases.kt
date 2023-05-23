package id.bluebird.vsm.domain.airport_assignment.domain.interactor

import id.bluebird.vsm.domain.airport_assignment.AirportAssignmentRepository
import id.bluebird.vsm.domain.airport_assignment.GetSubLocationStockCountDepartState
import id.bluebird.vsm.domain.airport_assignment.domain.cases.GetSubLocationStockCountDepart
import id.bluebird.vsm.domain.airport_assignment.model.StockCountModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class GetSubLocationStockCountDepartCases(
    private val airportAssignmentRepository: AirportAssignmentRepository
) : GetSubLocationStockCountDepart {
    override fun invoke(
        subLocationId: Long,
        locationId: Long,
        todayEpoch: Long
    ): Flow<GetSubLocationStockCountDepartState> = flow {
        val response = airportAssignmentRepository.getSubLocationStockCountDepart(
            subLocationId, locationId
        )
            .flowOn(Dispatchers.IO)
            .singleOrNull() ?: throw NullPointerException()

        val result = StockCountModel(
            response.stock,
            response.request,
            response.ritase
        )
        emit(GetSubLocationStockCountDepartState.Success(
            result = result
        ))
    }
}