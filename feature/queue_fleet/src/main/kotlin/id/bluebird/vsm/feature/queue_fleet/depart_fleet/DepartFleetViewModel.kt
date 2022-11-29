package id.bluebird.vsm.feature.queue_fleet.depart_fleet

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.domain.passenger.GetCurrentQueueState
import id.bluebird.vsm.domain.passenger.domain.cases.CurrentQueue
import id.bluebird.vsm.feature.queue_fleet.model.FleetItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class DepartFleetViewModel(
    private val currentQueue: CurrentQueue
): ViewModel() {
    private val _departFleetState = MutableSharedFlow<DepartFleetState>()
    val sharedDepartFleetState = _departFleetState.asSharedFlow()
    val showProgress: MutableLiveData<Boolean> = MutableLiveData(false)
    private var _departWithPassenger: MutableLiveData<Boolean> = MutableLiveData(true)
    private lateinit var _fleetItem: FleetItem

    @VisibleForTesting
    fun setStatusDepart(temp : Boolean) {
        _departWithPassenger.value = temp
    }

    @VisibleForTesting
    fun setFleetItem(temp : FleetItem) {
        _fleetItem = temp
    }


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

    fun departFleet(locationId: Long, subLocationId: Long) {
        if (_departWithPassenger.value == true) {
            getCurrentQueue(locationId, subLocationId)
        } else {
            _departWithPassenger.value?.let { withPassenger ->
                viewModelScope.launch {
                    _departFleetState.emit(
                        DepartFleetState.DepartFleet(
                            _fleetItem,
                            withPassenger,
                            ""
                        )
                    )
                }
            }
        }
    }

    fun showQueueList(currentQueueNumber: String, locationId: Long, subLocationId: Long) {
        viewModelScope.launch {
            _departFleetState.emit(DepartFleetState.SelectQueueToDepart(_fleetItem, currentQueueNumber, locationId, subLocationId))
        }
    }

    private fun getCurrentQueue(locationId: Long, subLocationId: Long) {
        viewModelScope.launch {
            _departFleetState.emit(DepartFleetState.OnProgressGetCurrentQueue)
            currentQueue
                .invoke(locationId)
                .flowOn(Dispatchers.Main)
                .catch { err ->
                    _departFleetState.emit(DepartFleetState.OnFailed(err))
                }
                .collect {
                    when (it) {
                        is GetCurrentQueueState.Success -> {
                            _departFleetState.emit(
                                DepartFleetState.DepartFleet(
                                    _fleetItem,
                                    true,
                                    it.currentQueueResult.number
                                )
                            )
                        }
                    }
                }
            showProgress.postValue(false)
        }
    }
}