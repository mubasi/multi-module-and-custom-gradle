package id.bluebird.vsm.domain.airport_assignment.domain.interactor

import id.bluebird.vsm.domain.airport_assignment.AirportAssignmentRepository
import id.bluebird.vsm.domain.airport_assignment.RequestTaxiDepartState
import id.bluebird.vsm.domain.airport_assignment.domain.cases.RequestTaxiDepart
import id.bluebird.vsm.domain.airport_assignment.model.RequestTaxiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class RequestTaxiDepartCases(
    private val airportAssignmentRepository: AirportAssignmentRepository
) : RequestTaxiDepart {
    private lateinit var flowCollector: FlowCollector<RequestTaxiDepartState>
    override fun invoke(
        requestFrom: Long,
        locationId: Long,
        count: Long
    ): Flow<RequestTaxiDepartState> = flow {
        flowCollector = this
        validate(count, requestFrom)
        val response = airportAssignmentRepository.requestTaxiDepart(
            requestFrom, locationId, count
        )
            .flowOn(Dispatchers.IO)
            .singleOrNull() ?: throw NullPointerException()

        val result = RequestTaxiModel(
            message = response.message,
            requestFrom = response.requestFrom,
            createdAt = response.createdAt,
            requestCount = response.requestCount
        )
        emit(RequestTaxiDepartState.Success(result))
    }

    private suspend fun validate(count: Long, subLocationId: Long) {
        if (count < 1) {
            flowCollector.emit(RequestTaxiDepartState.CountInvalid)
        }
        if (subLocationId < 1) {
            flowCollector.emit(RequestTaxiDepartState.SubLocationInvalid)
        }
    }
}