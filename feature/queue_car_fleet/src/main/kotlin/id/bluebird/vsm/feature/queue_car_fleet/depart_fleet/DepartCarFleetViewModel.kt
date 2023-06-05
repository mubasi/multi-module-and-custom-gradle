package id.bluebird.vsm.feature.queue_car_fleet.depart_fleet

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.feature.queue_car_fleet.model.CarFleetItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class DepartCarFleetViewModel: ViewModel() {
    private val _departCarFleetState = MutableSharedFlow<DepartCarFleetState>()
    val sharedDepartFleetState = _departCarFleetState.asSharedFlow()
    val showProgress: MutableLiveData<Boolean> = MutableLiveData(false)
    private var _departWithPassenger: MutableLiveData<Boolean> = MutableLiveData(true)
    private lateinit var _Car_fleetItem: CarFleetItem

    @VisibleForTesting
    fun setStatusDepart(temp : Boolean) {
        _departWithPassenger.value = temp
    }

    @VisibleForTesting
    fun setFleetItem(temp : CarFleetItem) {
        _Car_fleetItem = temp
    }


    fun setDepartStatus(withPassenger: Boolean) {
        _departWithPassenger.postValue(withPassenger)
    }

    fun init(carFleetItem: CarFleetItem) {
        _Car_fleetItem = carFleetItem
    }

    fun cancelDepart() {
        viewModelScope.launch {
            _departCarFleetState.emit(DepartCarFleetState.CancelDepartCar)
        }
    }

    fun departFleet(queueNumber: String = "") {
        _departWithPassenger.value?.let { withPassenger ->
            viewModelScope.launch {
                _departCarFleetState.emit(
                    DepartCarFleetState.DepartCarFleet(
                        _Car_fleetItem,
                        withPassenger,
                        queueNumber
                    )
                )
            }
        }
    }

    fun showQueueList(currentQueueNumber: String, locationId: Long, subLocationId: Long) {
        viewModelScope.launch {
            _departCarFleetState.emit(
                DepartCarFleetState.SelectQueueToDepartCar(
                    _Car_fleetItem,
                    currentQueueNumber,
                    locationId,
                    subLocationId
                )
            )
        }
    }
}