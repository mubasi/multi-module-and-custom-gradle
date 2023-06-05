package id.bluebird.vsm.feature.queue_car_fleet.add_fleet

import id.bluebird.vsm.feature.queue_car_fleet.model.CarFleetItem

sealed class AddCarFleetState {
    object OnProgressGetList : AddCarFleetState()
    object GetListEmpty : AddCarFleetState()
    data class GetListSuccess(val list: List<String>) : AddCarFleetState()
    data class SearchError(val err: Throwable) : AddCarFleetState()
    data class AddCarError(val err: Throwable) : AddCarFleetState()
    data class UpdateSelectPosition(val lastPosition: Int, val newPosition: Int) :
        AddCarFleetState()

    data class AddCarFleetSuccess(val carFleetItem: CarFleetItem) : AddCarFleetState()
    data class FinishSelectQueue(val number: String): AddCarFleetState()
    data class SuccessGetQueue(val list: List<String>): AddCarFleetState()
    data class QueueSearchError(val err: Throwable): AddCarFleetState()
}
