package id.bluebird.vsm.feature.queue_car_fleet.add_by_camera

sealed class AddCarFleetByCameraState {
    data class ProsesScan(val number: String): AddCarFleetByCameraState()
    object CancleScan : AddCarFleetByCameraState()
    object RepeatTakePicture : AddCarFleetByCameraState()
}