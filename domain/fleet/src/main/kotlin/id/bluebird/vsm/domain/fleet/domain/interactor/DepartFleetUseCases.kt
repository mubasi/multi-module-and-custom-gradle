package id.bluebird.vsm.domain.fleet.domain.interactor

import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.fleet.DepartFleetState
import id.bluebird.vsm.domain.fleet.FleetRepository
import id.bluebird.vsm.domain.fleet.domain.cases.DepartFleet
import id.bluebird.vsm.domain.fleet.model.FleetDepartResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class DepartFleetUseCases(private val repository: FleetRepository): DepartFleet {
    override fun invoke(
        subLocationId: Long,
        fleetNumber: String,
        isWithPassenger: Boolean,
        departFleetItems: List<Long>,
        queueNumber: String
    ): Flow<DepartFleetState> = flow {
        val response = repository.departFleet(
            UserUtils.getLocationId(),
            subLocationId,
            fleetNumber,
            isWithPassenger,
            departFleetItems,
            queueNumber
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