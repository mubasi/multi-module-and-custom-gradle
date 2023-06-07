package id.bluebird.vsm.feature.queue_car_fleet.request_fleet

sealed class RequestCarFleetDialogState {
    object CancelDialogCar : RequestCarFleetDialogState()
    object ProcessRequestCar : RequestCarFleetDialogState()
    data class FocusStateCar(val isFocus: Boolean) : RequestCarFleetDialogState()
    data class MessageError(val message: String) : RequestCarFleetDialogState()
    data class Err(val err: Throwable) : RequestCarFleetDialogState()
    data class RequestCarSuccess(val count: Long) : RequestCarFleetDialogState()

}
