package id.bluebird.vsm.feature.select_location.search_mall_location

import id.bluebird.vsm.feature.select_location.model.LocationModel

sealed class SearchMallLocationState {
    object Init : SearchMallLocationState()
    data class Idle(
        val list : List<LocationModel>
    ) : SearchMallLocationState()
    object Progress : SearchMallLocationState()
    data class OnItemClick(val locationModel: LocationModel, val position: Int) :
        SearchMallLocationState()
    data class SelectLocation(
        val locationId : Long, val subLocationId : Long
    ) : SearchMallLocationState()
}