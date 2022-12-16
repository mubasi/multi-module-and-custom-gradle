package id.bluebird.vsm.domain.fleet.domain.interactor

import id.bluebird.vsm.domain.fleet.FleetRepository
import id.bluebird.vsm.domain.fleet.SearchFleetState
import id.bluebird.vsm.domain.fleet.StringExtensions.getItemPerPage
import id.bluebird.vsm.domain.fleet.domain.cases.SearchFleet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class SearchFleetUseCases(private val _fleetRepository: FleetRepository) : SearchFleet {

    override fun invoke(param: String?): Flow<SearchFleetState> = flow {
        if (param.isNullOrEmpty()) {
            emit(SearchFleetState.EmptyResult)
        } else {
            val response = _fleetRepository.searchFleet(param, param.getItemPerPage())
                .flowOn(Dispatchers.IO)
                .singleOrNull() ?: throw  NullPointerException()
            if (response.fleetsList.isEmpty()) {
                emit(SearchFleetState.EmptyResult)
            } else {
                val result: MutableList<String> = mutableListOf()
                response.fleetsList.forEach {
                    result.add(it.fleetNumber)
                }
                emit(SearchFleetState.Success(result))
            }
        }
    }
}