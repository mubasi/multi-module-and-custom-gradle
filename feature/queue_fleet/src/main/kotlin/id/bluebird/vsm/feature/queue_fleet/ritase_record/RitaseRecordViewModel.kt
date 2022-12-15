package id.bluebird.vsm.feature.queue_fleet.ritase_record

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.domain.passenger.GetCurrentQueueState
import id.bluebird.vsm.domain.passenger.domain.cases.CurrentQueue
import id.bluebird.vsm.feature.queue_fleet.depart_fleet.DepartFleetState
import id.bluebird.vsm.feature.queue_fleet.model.FleetItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class RitaseRecordViewModel(
    private val currentQueue: CurrentQueue
): ViewModel() {
    private lateinit var _fleetItem: FleetItem
    private val _departFleetState = MutableSharedFlow<DepartFleetState>()
    val sharedDepartFleetState = _departFleetState.asSharedFlow()
    val showProgress: MutableLiveData<Boolean> = MutableLiveData(false)
    fun init(fleetItem: FleetItem, queueNumber: String, locationId: Long, subLocationId: Long) {
        _fleetItem = fleetItem
        if (queueNumber.isBlank())
            getCurrentQueue(locationId, subLocationId)
    }

    private fun getCurrentQueue(locationId: Long, subLocationId: Long) {
        viewModelScope.launch {
            _departFleetState.emit(DepartFleetState.OnProgressGetCurrentQueue)
            currentQueue
                .invoke(locationId)
                .flowOn(Dispatchers.Main)
                .catch { err ->
                    _departFleetState.emit(DepartFleetState.OnFailedGetCurrentQueue(err))
                }
                .collect {
                    when (it) {
                        is GetCurrentQueueState.Success -> {
                            _departFleetState.emit(
                                DepartFleetState.SuccessGetCurrentQueue(
                                    it.currentQueueResult.number
                                )
                            )
                        }
                    }
                }
            showProgress.postValue(false)
        }
    }

    fun departFleet(queueNumber: String = "") {
        viewModelScope.launch {
            _departFleetState.emit(
                DepartFleetState.DepartFleet(
                    _fleetItem,
                    true,
                    queueNumber
                )
            )
        }
    }

    fun showQueueList(currentQueueNumber: String, locationId: Long, subLocationId: Long) {
        viewModelScope.launch {
            _departFleetState.emit(DepartFleetState.SelectQueueToDepart(_fleetItem, currentQueueNumber, locationId, subLocationId))
        }
    }

    fun cancelDepart() {
        viewModelScope.launch {
            _departFleetState.emit(DepartFleetState.CancelDepart)
        }
    }
}