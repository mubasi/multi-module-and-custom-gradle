package id.bluebird.mall.domain_location

import id.bluebird.mall.domain_location.model.LocationsWithSub

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
    data class Success(val list: HashMap<Long, LocationsWithSub>) : GetLocationsWithSubState()
}
