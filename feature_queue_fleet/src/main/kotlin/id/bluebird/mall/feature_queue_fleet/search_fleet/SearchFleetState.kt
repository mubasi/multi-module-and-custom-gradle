package id.bluebird.mall.feature_queue_fleet.search_fleet

import id.bluebird.mall.feature_queue_fleet.model.FleetItem
import id.bluebird.mall.feature_queue_fleet.model.QueueItem

sealed class SearchFleetState {
    object SuccessDepartFleet : SearchFleetState()
    data class UpdateFleetItems(val list: List<FleetItem>) : SearchFleetState()
    data class RequestDepartFleetItem(val fleetItem: FleetItem): SearchFleetState()
}
