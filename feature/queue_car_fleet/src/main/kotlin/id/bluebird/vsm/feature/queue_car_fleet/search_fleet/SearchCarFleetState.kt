package id.bluebird.vsm.feature.queue_car_fleet.search_fleet

import id.bluebird.vsm.feature.queue_car_fleet.model.CarFleetItem

sealed class SearchCarFleetState {
    object SuccessDepartCarFleet : SearchCarFleetState()
    data class UpdateCarFleetItems(val list: List<CarFleetItem>) : SearchCarFleetState()
    data class RequestDepartCarFleetItem(val carFleetItem: CarFleetItem): SearchCarFleetState()
}
