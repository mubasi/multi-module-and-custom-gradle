package id.bluebird.vsm.domain.airport_assignment.domain.interactor

import id.bluebird.vsm.domain.airport_assignment.AirportAssignmentRepository
import id.bluebird.vsm.domain.airport_assignment.DispatchFleetAirportState
import id.bluebird.vsm.domain.airport_assignment.domain.cases.DispatchFleetAirport
import id.bluebird.vsm.domain.airport_assignment.model.DispatchFleetModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.singleOrNull
import proto.AssignmentOuterClass

class DispatchFleetAirportCases(
    private val airportAssignmentRepository: AirportAssignmentRepository
) : DispatchFleetAirport {

    private lateinit var _mDispatchFleetModel: DispatchFleetModel
    private lateinit var _mCallback: FlowCollector<DispatchFleetAirportState>

    override fun invoke(dispatchFleetModel: DispatchFleetModel): Flow<DispatchFleetAirportState> = flow {
        _mDispatchFleetModel = dispatchFleetModel
        _mCallback = this
        if (dispatchFleetModel.isPerimeter.not()) {
            dispatchFleetFromTerminal()
        } else {
            emit(DispatchFleetAirportState.WrongDispatchLocation)
        }
    }

    private suspend fun dispatchFleetFromTerminal() {
        with(_mDispatchFleetModel) {
            val result = airportAssignmentRepository.dispatchFleetFromTerminal(
                locationId = locationId,
                subLocationId = subLocationId,
                isArrived = isArrived,
                withPassenger = withPassenger(),
                stockIdList = _mDispatchFleetModel.fleetsAssignment
            ).singleOrNull() ?: throw NullPointerException()
            _mCallback.emit(
                if (isArrived.not()) {
                    DispatchFleetAirportState.SuccessArrived(getResultForArrivedFleet(response = result))
                } else {
                    DispatchFleetAirportState.SuccessDispatchFleet(result.taxiNoCount)
                }
            )
        }
    }

    private fun withPassenger(): Long = if (_mDispatchFleetModel.withPassenger) 1 else 0

    private fun getResultForArrivedFleet(response: AssignmentOuterClass.StockResponse): HashMap<String, Long> {
        val result: HashMap<String, Long> = hashMapOf()
        response.arrivedFleetList.forEach {
            result[it.taxiNo] = it.stockId
        }
        return result
    }
}