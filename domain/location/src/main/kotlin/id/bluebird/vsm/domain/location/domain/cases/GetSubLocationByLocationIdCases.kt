package id.bluebird.vsm.domain.location.domain.cases

import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.location.LocationDomainState
import id.bluebird.vsm.domain.location.LocationRepository
import id.bluebird.vsm.domain.location.domain.interactor.GetSubLocationByLocationId
import id.bluebird.vsm.domain.location.model.SubLocationResult
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