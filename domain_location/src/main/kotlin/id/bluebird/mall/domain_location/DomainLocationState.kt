package id.bluebird.mall.domain_location

sealed class LocationDomainState<out T : Any> {
    data class Success<out T : Any>(val value: T) : LocationDomainState<T>()

}

sealed interface LocationErrorState {
    object SubLocationIsEmpty : LocationErrorState, LocationDomainState<Nothing>()
    object LocationIsEmpty : LocationErrorState, LocationDomainState<Nothing>()
    object LocationIdWrong : LocationErrorState, LocationDomainState<Nothing>()
}