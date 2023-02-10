package id.bluebird.vsm.feature.select_location

import id.bluebird.vsm.feature.select_location.model.LocationModel

sealed class SelectLocationState {
    object OnProgressGetLocations : SelectLocationState()
    data class GetLocationSuccess(val locationModes: List<LocationModel>) : SelectLocationState()
    data class ToAssign(val isFleetMenu: Boolean) : SelectLocationState()
    data class OnItemClick(val locationModel: LocationModel, val position: Int) :
        SelectLocationState()
    object EmptyLocation : SelectLocationState()
    object SearchLocation : SelectLocationState()
    data class OnError(val error: Throwable) : SelectLocationState()
    data class ToAssignFromSearach(val isFleetMenu: Boolean) : SelectLocationState()
    data class FilterFleet(val result: List<LocationModel>) : SelectLocationState()
    object ErrorFilter : SelectLocationState()
}
