package id.bluebird.vsm.feature.queue_fleet.add_fleet

import id.bluebird.vsm.feature.queue_fleet.model.FleetItem

sealed class AddFleetState {
    object OnProgressGetList : AddFleetState()
    object GetListEmpty : AddFleetState()
    data class GetListSuccess(val list: List<String>) : AddFleetState()
    data class SearchError(val err: Throwable) : AddFleetState()
    data class AddError(val err: Throwable) : AddFleetState()
    data class UpdateSelectPosition(val lastPosition: Int, val newPosition: Int) :
        AddFleetState()

    data class AddFleetSuccess(val fleetItem: FleetItem) : AddFleetState()
    data class FinishSelectQueue(val number: String): AddFleetState()
    data class SuccessGetQueue(val list: List<String>): AddFleetState()
    data class QueueSearchError(val err: Throwable): AddFleetState()
}
