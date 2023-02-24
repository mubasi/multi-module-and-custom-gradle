package id.bluebird.vsm.feature.home.ritase_fleet

import id.bluebird.vsm.feature.home.model.FleetItemList

sealed class RitaseFleetState {
    object CancleFleet : RitaseFleetState()
    object ProsesListFleet : RitaseFleetState()
    object CurrentQueueNotFound : RitaseFleetState()
    object GetListEmpty : RitaseFleetState()
    object FleetNotSelected : RitaseFleetState()
    data class FailedGetList(val throwable: Throwable) : RitaseFleetState()
    data class GetListSuccess(val result : List<FleetItemList>) : RitaseFleetState()
    data class FilterFleet(val result : List<FleetItemList>) : RitaseFleetState()
    object FilterFleetFailed : RitaseFleetState()
    data class SuccessSaveFleet(val fleetNumber: String) : RitaseFleetState()
    object ProsesSaveFleet : RitaseFleetState()
    data class UpdateSelectPosition(val lastPosition: Int, val newPosition: Int) :
        RitaseFleetState()
}