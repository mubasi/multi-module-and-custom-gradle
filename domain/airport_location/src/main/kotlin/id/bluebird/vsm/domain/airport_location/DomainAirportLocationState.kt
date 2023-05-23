package id.bluebird.vsm.domain.airport_location

import id.bluebird.vsm.domain.airport_location.model.CreateLocationRequestModel
import id.bluebird.vsm.domain.airport_location.model.GetSubLocationByIdModel

sealed class GetListSublocationAirportState {
    data class Success(
        val result : GetSubLocationByIdModel
    ) : GetListSublocationAirportState()
    object EmptyResult : GetListSublocationAirportState()
}

sealed class GetLocationAirportState {
    data class Success(
        val result : List<CreateLocationRequestModel>
    ) : GetLocationAirportState()
    object EmptyResult : GetLocationAirportState()
}
