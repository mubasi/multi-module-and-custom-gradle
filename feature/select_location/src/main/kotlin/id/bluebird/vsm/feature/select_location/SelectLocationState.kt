package id.bluebird.vsm.feature.select_location

import id.bluebird.vsm.feature.select_location.model.LocationModel
import id.bluebird.vsm.feature.select_location.model.SubLocationModelCache

sealed class SelectLocationState {
    object UserAirport : SelectLocationState()
    object UserOutlet : SelectLocationState()
    object OnProgressGetLocations : SelectLocationState()
    data class GetLocationSuccess(val locationModes: List<LocationModel>) : SelectLocationState()
    data class GetSubLocationSuccess(val locationModes: List<SubLocationModelCache>) :
        SelectLocationState()

    data class ToAssign(val isFleetMenu: Boolean) : SelectLocationState()
    object ToAssignAirport : SelectLocationState()
    data class OnItemClick(val locationModel: LocationModel, val position: Int) :
        SelectLocationState()

    object EmptyLocation : SelectLocationState()
    data class SearchLocation(
        val isAirport : Boolean
    ) : SelectLocationState()
    data class OnError(val error: Throwable) : SelectLocationState()
    data class ToAssignFromSearch(val isFleetMenu: Boolean) : SelectLocationState()
    object ToAssignFromSearchAirport : SelectLocationState()
    data class FilterFleet(val result: List<LocationModel>) : SelectLocationState()
    data class FilterLocationAirport(val result: List<SubLocationModelCache>) : SelectLocationState()
    object ErrorFilter : SelectLocationState()
}
