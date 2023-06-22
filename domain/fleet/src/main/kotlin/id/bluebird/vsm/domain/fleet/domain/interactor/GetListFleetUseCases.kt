package id.bluebird.vsm.domain.fleet.domain.interactor

import id.bluebird.vsm.domain.fleet.FleetRepository
import id.bluebird.vsm.domain.fleet.GetListFleetState
import id.bluebird.vsm.domain.fleet.domain.cases.GetListFleet
import id.bluebird.vsm.domain.fleet.model.FleetItemResult
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
                    arriveAt = it.createdAt,
                    sequence = it.fleetSequence
                )
                fleetItemResults.add(fleetItemResult)
            }
            emit(GetListFleetState.Success(fleetItemResults))
        }
    }
}