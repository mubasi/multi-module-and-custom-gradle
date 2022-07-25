package id.bluebird.mall.domain_fleet.domain.interactor

import id.bluebird.mall.domain_fleet.FleetRepository
import id.bluebird.mall.domain_fleet.SearchFleetState
import id.bluebird.mall.domain_fleet.StringExtensions.getItemPerPage
import id.bluebird.mall.domain_fleet.domain.cases.SearchFleet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class SearchFleetUseCases(private val _fleetRepository: FleetRepository) : SearchFleet {

    override fun invoke(param: String?): Flow<SearchFleetState> = flow {
        val response = _fleetRepository.searchFleet(param ?: "", param.getItemPerPage())
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