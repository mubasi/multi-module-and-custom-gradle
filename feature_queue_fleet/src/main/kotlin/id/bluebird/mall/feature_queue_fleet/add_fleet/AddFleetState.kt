package id.bluebird.mall.feature_queue_fleet.add_fleet

sealed class AddFleetState {
    object OnProgressGetList : AddFleetState()
    object GetListEmpty : AddFleetState()
    data class GetListSuccess(val list: List<String>) : AddFleetState()
    data class SearchError(val err: Throwable) : AddFleetState()
    data class AddError(val err: Throwable) : AddFleetState()
    data class UpdateSelectPosition(val lastPosition: Int, val newPosition: Int) :
        AddFleetState()

    data class AddFleetSuccess(val fleetNumber: String) : AddFleetState()
}
