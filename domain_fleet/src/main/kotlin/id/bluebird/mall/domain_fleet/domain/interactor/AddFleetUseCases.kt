package id.bluebird.mall.domain_fleet.domain.interactor

import id.bluebird.mall.core.utils.hawk.UserUtils
import id.bluebird.mall.domain_fleet.AddFleetState
import id.bluebird.mall.domain_fleet.FleetRepository
import id.bluebird.mall.domain_fleet.domain.cases.AddFleet
import id.bluebird.mall.domain_fleet.model.FleetItemResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class AddFleetUseCases(private val fleetRepository: FleetRepository) : AddFleet {
    override fun invoke(
        fleetNumber: String,
        subLocationId: Long
    ): Flow<AddFleetState> = flow {
        val response = fleetRepository.addFleet(
            fleetNumber = fleetNumber,
            subLocationId = subLocationId,
            UserUtils.getLocationId()
        )
            .flowOn(Dispatchers.IO)
            .singleOrNull() ?: throw NullPointerException()
        val fleetItemResult = FleetItemResult(
            fleetId = response.stockId,
            fleetName = fleetNumber.uppercase(),
            arriveAt = response.createdAt
        )
        emit(AddFleetState.Success(fleetItemResult))
    }

}