package id.bluebird.vsm.domain.airport_location.domain.interactor

import id.bluebird.vsm.domain.airport_location.AirportLocationRepository
import id.bluebird.vsm.domain.airport_location.GetLocationAirportState
import id.bluebird.vsm.domain.airport_location.domain.cases.GetLocationAirport
import id.bluebird.vsm.domain.airport_location.model.CreateLocationRequestModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.singleOrNull

class GetLocationAirportCases(
    private val airportLocationRepository: AirportLocationRepository
) : GetLocationAirport {
    override fun invoke(): Flow<GetLocationAirportState> = flow {
        val response = airportLocationRepository.getLocationAirport()
            .flowOn(Dispatchers.IO)
            .singleOrNull() ?: throw NullPointerException()

        if (response.listLocationsList.isEmpty()) {
            emit(
                GetLocationAirportState.EmptyResult
            )
        } else {
            val result: ArrayList<CreateLocationRequestModel> = ArrayList()
            response.listLocationsList.map {
                result.add(
                    CreateLocationRequestModel(
                        id = it.id,
                        locationName = it.locationName,
                        isActive = it.isActive,
                        createdAt = it.createdAt,
                        modifiedAt = it.modifiedAt,
                        createdBy = it.createdBy,
                        modifiedBy = it.modifiedBy,
                        codeArea = it.codeArea,
                        intervalReset = it.intervalReset
                    )
                )
            }
            emit(
                GetLocationAirportState.Success(result)
            )
        }
    }

}