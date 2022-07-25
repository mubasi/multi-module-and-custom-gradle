package id.bluebird.mall.domain_fleet.domain.interactor

import id.bluebird.mall.domain_fleet.FleetRepository
import id.bluebird.mall.domain_fleet.GetListFleetState
import id.bluebird.mall.domain_fleet.domain.cases.GetListFleet
import id.bluebird.mall.domain_fleet.model.FleetItemResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class GetListFleetUseCases(private val _fleetRepo: FleetRepository) : GetListFleet {
    override fun invoke(subLocationId: Long): Flow<GetListFleetState> = flow {
        val response = _fleetRepo.getListFleet(subLocationId)
            .flowOn(Dispatchers.IO)
            .singleOrNull() ?: throw NullPointerException()
        if (response.fleetListCount < 1) {
            emit(GetListFleetState.EmptyResult)
        } else {
            val fleetItemResults: MutableList<FleetItemResult> = mutableListOf()
            response.fleetListList.forEach {
                val fleetItemResult = FleetItemResult(
                    fleetId = it.fleetId,
                    fleetName = it.taxiNo,
                    arriveAt = it.createdAt
                )
                fleetItemResults.add(fleetItemResult)
            }
            emit(GetListFleetState.Success(fleetItemResults))
        }
    }
}