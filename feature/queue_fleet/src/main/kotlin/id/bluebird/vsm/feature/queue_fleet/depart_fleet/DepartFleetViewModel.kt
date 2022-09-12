package id.bluebird.vsm.feature.queue_fleet.depart_fleet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.feature.queue_fleet.model.FleetItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class DepartFleetViewModel: ViewModel() {
    private val _departFleetState = MutableSharedFlow<DepartFleetState>()
    val sharedDepartFleetState = _departFleetState.asSharedFlow()
    private val _departWithPassenger: MutableLiveData<Boolean> = MutableLiveData(true)
    private lateinit var _fleetItem: FleetItem

    fun setDepartStatus(withPassenger: Boolean) {
        _departWithPassenger.postValue(withPassenger)
    }

    fun init(fleetItem: FleetItem) {
        _fleetItem = fleetItem
    }

    fun cancelDepart() {
        viewModelScope.launch {
            _departFleetState.emit(DepartFleetState.CancelDepart)
        }
    }

    fun departFleet(currentQueueNumber: String) {
        _departWithPassenger.value?.let { withPassenger ->
            viewModelScope.launch {
                _departFleetState.emit(DepartFleetState.DepartFleet(_fleetItem, withPassenger, currentQueueNumber))
            }
        }
    }

    fun showQueueList(currentQueueNumber: String) {
        viewModelScope.launch {
            _departFleetState.emit(DepartFleetState.SelectQueueToDepart(_fleetItem, currentQueueNumber))
        }
    }
}