package id.bluebird.vsm.feature.user_management.search_location

import id.bluebird.vsm.feature.user_management.search_location.model.Location

sealed class SearchLocationState {
    object OnProgressGetList: SearchLocationState()
    object FailedGetList: SearchLocationState()
    object EmptyList: SearchLocationState()
    data class Success(val data: List<Location>): SearchLocationState()
    data class UpdateSelectedLocation(val position: Int, val lasPosition: Int, val item: Location?): SearchLocationState()
    data class SetSelected(val item: Location): SearchLocationState()
}
