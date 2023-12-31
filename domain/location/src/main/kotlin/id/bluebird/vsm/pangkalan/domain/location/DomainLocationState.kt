package id.bluebird.vsm.domain.location

import id.bluebird.vsm.domain.location.model.GetLocationQrCodeResult
import id.bluebird.vsm.domain.location.model.LocationsWithSub

sealed class LocationDomainState<out T : Any> {
    data class Success<out T : Any>(val value: T) : LocationDomainState<T>()
    object Empty : LocationDomainState<Nothing>()
}

sealed interface LocationErrorState {
    object SubLocationIsEmpty : LocationErrorState, LocationDomainState<Nothing>()
    object LocationIsEmpty : LocationErrorState, LocationDomainState<Nothing>()
    object LocationIdWrong : LocationErrorState, LocationDomainState<Nothing>()
}

sealed class GetLocationsWithSubState {
    data class Success(val list: List<LocationsWithSub>) : GetLocationsWithSubState()
}

sealed class GetLocationQrCodeState {
    data class Success(val result: GetLocationQrCodeResult) : GetLocationQrCodeState()
}
