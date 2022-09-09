package id.bluebird.mall.feature.select_location

import id.bluebird.mall.feature.select_location.model.LocationModel

sealed class SelectLocationState {
    object OnProgressGetLocations : SelectLocationState()
    data class FailedGetLocation(val err: Throwable) : SelectLocationState()
    data class GetLocationSuccess(val locationModes: List<LocationModel>) : SelectLocationState()
    data class ToAssign(val isFleetMenu: Boolean) : SelectLocationState()
    data class OnItemClick(val locationModel: LocationModel, val position: Int) :
        SelectLocationState()

    data class OnError(val error: Throwable) : SelectLocationState()
}
