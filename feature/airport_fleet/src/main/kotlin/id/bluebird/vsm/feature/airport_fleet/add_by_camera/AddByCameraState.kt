package id.bluebird.vsm.feature.airport_fleet.add_by_camera

sealed class AddByCameraState {
    data class ProsesScan(val number: String): AddByCameraState()
    object CancleScan : AddByCameraState()
    object RepeatTakePicture : AddByCameraState()
    data class OnError(val throwable: Throwable) : AddByCameraState()
}