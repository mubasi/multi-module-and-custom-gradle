package id.bluebird.mall.domain_location.domain.cases

import id.bluebird.mall.domain_location.GetLocationsWithSubState
import id.bluebird.mall.domain_location.LocationRepository
import id.bluebird.mall.domain_location.domain.interactor.GetLocationsWithSub
import id.bluebird.mall.domain_location.model.LocationsWithSub
import id.bluebird.mall.domain_location.model.SubLocationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.lang.NullPointerException

class GetLocationsWithSubUseCases(
    private val locationRepository: LocationRepository
) : GetLocationsWithSub {
    override fun invoke(): Flow<GetLocationsWithSubState> = flow {
        val result: HashMap<Long, LocationsWithSub> = HashMap()
        locationRepository.getLocations()
            .flowOn(Dispatchers.IO)
            .zip(locationRepository.getSubLocations()) { getLocationsResponse, getSubLocationsResponse ->
                getLocationsResponse.listLocationsList.forEach {
                    val item =
                        LocationsWithSub(locationId = it.id, locationName = it.locationName)
                    result[item.locationId] = item
                }
                getSubLocationsResponse.subLocationsList.forEach {
                    result[it.locationId]?.list?.add(
                        SubLocationResult(
                            id = it.subLocationId,
                            name = it.subLocationName
                        )
                    )
                }
            }
            .singleOrNull() ?: throw  NullPointerException()
        emit(GetLocationsWithSubState.Success(result))
    }
}