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
    companion object {
        const val EMPTY_STRING = ""
    }
    private val _departCarFleetState = MutableSharedFlow<DepartCarFleetState>()
    val sharedDepartFleetState = _departCarFleetState.asSharedFlow()
    val showProgress: MutableLiveData<Boolean> = MutableLiveData(false)
    var departWithPassenger: MutableLiveData<Boolean> = MutableLiveData(true)
    private lateinit var carFleetItem: CarFleetItem

    @VisibleForTesting
    fun setStatusDepart(temp : Boolean) {
        departWithPassenger.value = temp
    }

    @VisibleForTesting
    fun setFleetItem(temp : CarFleetItem) {
        carFleetItem = temp
    }


    fun setDepartStatus(withPassenger: Boolean) {
        departWithPassenger.postValue(withPassenger)
    }

    fun init(result: CarFleetItem) {
        carFleetItem = result
    }

    fun cancelDepart() {
        viewModelScope.launch {
            _departCarFleetState.emit(DepartCarFleetState.CancelDepartCar)
        }
    }

    fun departFleet(queueNumber: String = EMPTY_STRING) {
        departWithPassenger.value?.let { withPassenger ->
            viewModelScope.launch {
                _departCarFleetState.emit(
                    DepartCarFleetState.DepartCarFleet(
                        carFleetItem,
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
                    carFleetItem,
                    currentQueueNumber,
                    locationId,
                    subLocationId
                )
            )
        }
    }
}