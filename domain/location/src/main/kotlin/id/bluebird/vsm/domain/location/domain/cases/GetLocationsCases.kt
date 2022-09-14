package id.bluebird.vsm.domain.location.domain.cases

import id.bluebird.vsm.domain.location.LocationDomainState
import id.bluebird.vsm.domain.location.LocationRepository
import id.bluebird.vsm.domain.location.domain.interactor.GetLocations
import id.bluebird.vsm.domain.location.model.LocationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class GetLocationsCases(private val locationRepository: LocationRepository) : GetLocations {
    override fun invoke(): Flow<LocationDomainState<List<LocationResult>>> =
        flow {
            val response = locationRepository
                .getLocations()
                .flowOn(Dispatchers.IO)
                .singleOrNull() ?: throw NullPointerException()
            if (response.listLocationsCount < 1) {
                emit(LocationDomainState.Empty)
            } else {
                val result = response.listLocationsList.map {
                    LocationResult(
                        id = it.id,
                        locationName = it.locationName,
                        isActive = it.isActive == 1,
                        codeArea = it.codeArea
                    )
                }

                emit(LocationDomainState.Success(result))
            }
        }
}