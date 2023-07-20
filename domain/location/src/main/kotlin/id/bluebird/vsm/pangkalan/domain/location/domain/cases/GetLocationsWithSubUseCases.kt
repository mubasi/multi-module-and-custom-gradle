package id.bluebird.vsm.domain.location.domain.cases

import id.bluebird.vsm.domain.location.GetLocationsWithSubState
import id.bluebird.vsm.domain.location.LocationRepository
import id.bluebird.vsm.domain.location.domain.interactor.GetLocationsWithSub
import id.bluebird.vsm.domain.location.model.LocationsWithSub
import id.bluebird.vsm.domain.location.model.SubLocationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class GetLocationsWithSubUseCases(
    private val locationRepository: LocationRepository
) : GetLocationsWithSub {

    override fun invoke(): Flow<GetLocationsWithSubState> = flow {
        var result: List<LocationsWithSub> = ArrayList()
        locationRepository.getLocations()
            .flowOn(Dispatchers.IO)
            .zip(locationRepository.getSubLocations()) { getLocationsResponse, getSubLocationsResponse ->
                val tempResult: HashMap<Long, LocationsWithSub> = HashMap()
                getLocationsResponse.listLocationsList.forEach {
                    tempResult[it.id] =
                        LocationsWithSub(locationId = it.id, locationName = it.locationName)
                }
                getSubLocationsResponse.subLocationsList.forEach {
                    if (it.prefix.isNotEmpty() && !it.isDeposition) {
                        tempResult[it.locationId]?.list?.add(
                            SubLocationResult(
                                id = it.subLocationId,
                                name = it.subLocationName,
                                prefix = it.prefix,
                                isDeposition = it.isDeposition,
                            )
                        )
                    }
                }
                result = tempResult.values.filter { it.list.isNotEmpty() }.toList()
            }
            .singleOrNull() ?: throw NullPointerException()
        emit(GetLocationsWithSubState.Success(result))
    }
}