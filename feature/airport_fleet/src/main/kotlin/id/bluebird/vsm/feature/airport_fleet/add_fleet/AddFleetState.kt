package id.bluebird.vsm.feature.airport_fleet.add_fleet

sealed class AddFleetState {
    object FleetsReset : AddFleetState()
    object ProgressSearch : AddFleetState()
    data class AddFleetSuccess(val fleetNumber: String) : AddFleetState()
    data class SearchFleetSuccess(val list: List<String>) : AddFleetState()
    data class AddFleetError(val throwable: Throwable) : AddFleetState()
    data class SearchFleetError(val throwable: Throwable) : AddFleetState()
    data class ShowTuDialog(val fleetNumber: String) : AddFleetState()
    data class ShowDialogAddFleet(val fleetNumber: String) : AddFleetState()
}
