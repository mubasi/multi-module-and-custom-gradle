package id.bluebird.mall.domain_fleet

sealed class DomainFleetState<out T : Any> {
    data class Success<out T : Any>(val value: T) : DomainFleetState<T>()
}