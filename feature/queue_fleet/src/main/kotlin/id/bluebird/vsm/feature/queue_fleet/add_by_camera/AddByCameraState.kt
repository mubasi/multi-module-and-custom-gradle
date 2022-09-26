package id.bluebird.vsm.feature.queue_fleet.add_by_camera

sealed class AddByCameraState {
    data class ProsesScan(val number: String): AddByCameraState()
    object CancleScan : AddByCameraState()
    object RepeatTakePicture : AddByCameraState()
}