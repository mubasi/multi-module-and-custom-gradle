package id.bluebird.vsm.feature.queue_fleet.search_fleet

import id.bluebird.vsm.feature.queue_fleet.model.FleetItem

sealed class SearchFleetState {
    object SuccessDepartFleet : SearchFleetState()
    data class UpdateFleetItems(val list: List<FleetItem>) : SearchFleetState()
    data class RequestDepartFleetItem(val fleetItem: FleetItem): SearchFleetState()
}
