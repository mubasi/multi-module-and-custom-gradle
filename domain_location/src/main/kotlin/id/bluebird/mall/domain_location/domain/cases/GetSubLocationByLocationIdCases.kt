package id.bluebird.mall.domain_location.domain.cases

import id.bluebird.mall.core.utils.hawk.UserUtils
import id.bluebird.mall.domain_location.LocationDomainState
import id.bluebird.mall.domain_location.LocationRepository
import id.bluebird.mall.domain_location.domain.interactor.GetSubLocationByLocationId
import id.bluebird.mall.domain_location.model.SubLocationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class GetSubLocationByLocationIdCases(private val locationRepository: LocationRepository) :
    GetSubLocationByLocationId {
    override fun invoke(id: Long): Flow<LocationDomainState<List<SubLocationResult>>> = flow {
        var locationId = id
        if (id < 1) {
            locationId = UserUtils.getLocationId()
        }
        val response = locationRepository.getSubLocationByLocationId(locationId)
            .flowOn(Dispatchers.IO)
            .singleOrNull() ?: throw NullPointerException()
        val result = mutableListOf<SubLocationResult>()
        response.subLocationListList.forEach {
            result.add(SubLocationResult(it.subLocationId, it.subLocationName))
        }
        emit(LocationDomainState.Success(result))
    }
}