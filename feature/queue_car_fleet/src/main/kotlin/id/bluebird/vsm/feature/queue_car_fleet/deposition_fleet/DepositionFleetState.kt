package id.bluebird.vsm.feature.queue_car_fleet.deposition_fleet

import id.bluebird.vsm.feature.queue_car_fleet.model.CarFleetItem

sealed class DepositionFleetState {
    object ProgressGetList : DepositionFleetState()
    data class GetListSuccess(
        val result : List<CarFleetItem>
    ) : DepositionFleetState()
    data class FailedGetList(
        val result: Throwable
    ) : DepositionFleetState()
    object GetListEmpty : DepositionFleetState()
}