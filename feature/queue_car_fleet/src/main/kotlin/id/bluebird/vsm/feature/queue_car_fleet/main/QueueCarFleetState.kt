package id.bluebird.vsm.feature.queue_car_fleet.main

import id.bluebird.vsm.feature.queue_car_fleet.model.CarFleetItem

sealed class QueueCarFleetState {
    object ProgressHolder: QueueCarFleetState()
    object ToSelectLocation : QueueCarFleetState()
    object Idle : QueueCarFleetState()
    object ProgressGetUser : QueueCarFleetState()
    object ProgressGetCarFleetList : QueueCarFleetState()
    object ProgressDepartCarFleet : QueueCarFleetState()
    object GetUserInfoSuccess : QueueCarFleetState()
    object GetListEmpty : QueueCarFleetState()
    data class RecordRitaseToDepart(
        val fleet: CarFleetItem,
        val locationId: Long,
        val subLocationId: Long,
        val queueId: String
    ) : QueueCarFleetState()

    data class SearchCarFleet(val subLocationId: Long, val list: List<CarFleetItem>) : QueueCarFleetState()
    data class NotifyDataCarFleetChanged(val list: List<CarFleetItem>): QueueCarFleetState()
    data class AddCarFleet(val subLocationId: Long) : QueueCarFleetState()
    data class ShowRequestCarFleet(val subLocationId: Long) : QueueCarFleetState()
    data class FailedGetUser(val message: String) : QueueCarFleetState()
    data class FailedGetCounter(val message: String) : QueueCarFleetState()
    data class GetListSuccess(val list: List<CarFleetItem>) : QueueCarFleetState()
    data class FailedGetList(val throwable: Throwable) : QueueCarFleetState()
    data class FailedGetQueueCar(val throwable: Throwable) : QueueCarFleetState()
    data class RequestDepartCarFleet(val fleet: CarFleetItem, val locationId: Long, val subLocationId: Long) : QueueCarFleetState()
    data class SuccessDepartCarFleet(val fleetNumber: String, val isWithPassenger: Boolean) :
        QueueCarFleetState()
    data class AddFleetSuccess(val list: List<CarFleetItem>, val itemAdd : CarFleetItem): QueueCarFleetState()

    data class SearchQueueToDepartCar(
        val fleet: CarFleetItem,
        val locationId: Long,
        val subLocationId: Long,
        val currentQueueId: String
    ) : QueueCarFleetState()

    data class FailedDepart(val message: String) : QueueCarFleetState()

    data class GoToQrCodeScreen(
        val locationId: Long,
        val subLocationId: Long,
        val titleLocation: String
    ) : QueueCarFleetState()
    data class GotoDepositionScreen(
        val title : String,
        val subLocationId: Long,
        val idDeposition: Long,
        val depositionStock : Long
    ) : QueueCarFleetState()
}