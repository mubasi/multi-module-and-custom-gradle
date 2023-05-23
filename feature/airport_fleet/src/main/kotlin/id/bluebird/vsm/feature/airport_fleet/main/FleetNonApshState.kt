package id.bluebird.vsm.feature.airport_fleet.main

import id.bluebird.vsm.feature.airport_fleet.main.model.AssignmentCarCache
import id.bluebird.vsm.feature.airport_fleet.main.model.FleetItemCar

sealed class FleetNonApshState {
    object ProgressHolder : FleetNonApshState()
    object ToSelectLocation : FleetNonApshState()
    object OnEmptyData : FleetNonApshState()
    object OnProgress : FleetNonApshState()
    object GetCountSuccess : FleetNonApshState()
    object Idle : FleetNonApshState()
    object OnSuccess : FleetNonApshState()
    data class OnError(val err : Throwable) : FleetNonApshState()
    data class IntentToAddFleet(
        val isPerimeter:Boolean,
        val subLocationId: Long,
        val isWing: Boolean = false
    ) : FleetNonApshState()
    data class TakePicture(
        val subLocationId: Long
    ) : FleetNonApshState()
    data class DialogRequest(
        val requestToId : Long,
        val subLocationId : Long,
        val subLocationName : String
    ) : FleetNonApshState()
    data class DispatchCar(
        val status: Boolean
    ) : FleetNonApshState()
    data class SuccessDispatchFleet(
        val message : Any,
        val isNonTerminal : Boolean,
        val isWithPassenger : Boolean
    ) : FleetNonApshState()
    data class SendCar(
        val result : ArrayList<AssignmentCarCache>
    ) : FleetNonApshState()
    data class SuccessArrived(
        val message : Any,
        val isStatusArrived : Boolean,
        val isWithPassenger : Boolean,
    ) : FleetNonApshState()
}
