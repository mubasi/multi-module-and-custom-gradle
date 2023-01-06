package id.bluebird.vsm.domain.fleet.domain.interactor

import id.bluebird.vsm.domain.fleet.DepartFleetState
import id.bluebird.vsm.domain.fleet.FleetRepository
import id.bluebird.vsm.domain.fleet.domain.cases.DepartFleet
import id.bluebird.vsm.domain.fleet.model.FleetDepartResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class DepartFleetUseCases(private val repository: FleetRepository): DepartFleet {
    override fun invoke(
        locationId: Long,
        subLocationId: Long,
        fleetNumber: String,
        isWithPassenger: Boolean,
        departFleetItems: List<Long>,
        queueNumber: String
    ): Flow<DepartFleetState> = flow {
        val response = repository.departFleet(
            locationId = locationId,
            subLocationId = subLocationId,
            fleetNumber = fleetNumber,
            isWithPassenger = isWithPassenger,
            departFleetItems = departFleetItems,
            queueNumber = queueNumber
        )
            .flowOn(Dispatchers.IO)
            .singleOrNull() ?: throw NullPointerException()

        val departFleetItem = FleetDepartResult(
            taxiNo = try {
                response.taxiNoList.first()
            } catch (e: NoSuchElementException) {fleetNumber.uppercase()},
            message = response.message,
            stockType = response.stockType,
            stockId = response.stockId.toString(),
            createdAt = response.createdAt
        )

        emit(DepartFleetState.Success(departFleetItem))
    }
}