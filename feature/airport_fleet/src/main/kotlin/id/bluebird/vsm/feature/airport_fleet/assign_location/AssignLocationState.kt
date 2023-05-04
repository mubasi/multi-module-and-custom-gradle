package id.bluebird.vsm.feature.airport_fleet.assign_location

import id.bluebird.vsm.feature.airport_fleet.main.model.AssignLocationModel


sealed class AssignLocationState {
    object Back : AssignLocationState()
    object SendFleetOnProgress : AssignLocationState()
    data class SendCarSuccess(val countData : Int, val countFleet: Any, val location: AssignLocationModel) :
        AssignLocationState()
    object LocationIsNoSelected : AssignLocationState()
    data class OnError(val t: Throwable) : AssignLocationState()
    object GetListProsess : AssignLocationState()
    data class GetListSuccess(
        val result : List<AssignLocationModel>
    ) : AssignLocationState()
    object SelectedCarIsEmpty : AssignLocationState()
    data class SendCarFromAirport(
        val countData : Int,
        val message: Any,
        val isWithPassenger: Boolean,
        val isStatusArrived: Boolean
    ) :
        AssignLocationState()
}