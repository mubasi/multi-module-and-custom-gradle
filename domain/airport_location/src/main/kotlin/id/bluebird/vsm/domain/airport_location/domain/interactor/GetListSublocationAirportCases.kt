package id.bluebird.vsm.domain.airport_location.domain.interactor

import id.bluebird.vsm.domain.airport_location.AirportLocationRepository
import id.bluebird.vsm.domain.airport_location.GetListSublocationAirportState
import id.bluebird.vsm.domain.airport_location.domain.cases.GetListSublocationAirport
import id.bluebird.vsm.domain.airport_location.model.GetSubLocationByIdModel
import id.bluebird.vsm.domain.airport_location.model.SubLocationItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.singleOrNull

class GetListSublocationAirportCases(
    private val airportLocationRepository: AirportLocationRepository
) : GetListSublocationAirport {
    override fun invoke(
        locationId: Long,
        showDeposition: Boolean,
        showWingsChild: Boolean
    ): Flow<GetListSublocationAirportState> = flow {
        val response = airportLocationRepository.getSubLocationByLocationIdAirport(
            locationId, showDeposition, showWingsChild
        )
            .singleOrNull() ?: throw NullPointerException()

        if (response.subLocationListList.isEmpty()) {
            emit(
                GetListSublocationAirportState.EmptyResult
            )
        } else {
            val subLocationList: ArrayList<SubLocationItemModel> = ArrayList()
            response.subLocationListList.map {
                subLocationList.add(
                    SubLocationItemModel(
                        subLocationId = it.subLocationId,
                        subLocationName = it.subLocationName,
                        subLocationType = it.subLocationType,
                        isDeposition = it.isDeposistion,
                        isWings = it.isWings
                    )
                )
            }
            val result = GetSubLocationByIdModel(
                locationId = response.locationId,
                locationName = response.locationName,
                codeArea = response.codeArea,
                subLocationList
            )
            emit(
                GetListSublocationAirportState.Success(result)
            )
        }
    }

}