package id.bluebird.vsm.feature.queue_car_fleet.ritase_record

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.domain.passenger.GetCurrentQueueState
import id.bluebird.vsm.domain.passenger.domain.cases.CurrentQueue
import id.bluebird.vsm.feature.queue_car_fleet.depart_fleet.DepartCarFleetState
import id.bluebird.vsm.feature.queue_car_fleet.model.CarFleetItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class RitaseCarFleetRecordViewModel(
    private val currentQueue: CurrentQueue
): ViewModel() {
    private lateinit var _Car_fleetItem: CarFleetItem
    private val _departCarFleetState = MutableSharedFlow<DepartCarFleetState>()
    val sharedDepartFleetState = _departCarFleetState.asSharedFlow()
    val showProgress: MutableLiveData<Boolean> = MutableLiveData(false)
    fun init(carFleetItem: CarFleetItem, queueNumber: String, locationId: Long, subLocationId: Long) {
        _Car_fleetItem = carFleetItem
        if (queueNumber.isBlank())
            getCurrentQueue(locationId, subLocationId)
    }

    private fun getCurrentQueue(locationId: Long, subLocationId: Long) {
        viewModelScope.launch {
            _departCarFleetState.emit(DepartCarFleetState.OnProgressGetCurrentQueue)
            currentQueue
                .invoke(locationId, subLocationId)
                .flowOn(Dispatchers.Main)
                .catch { err ->
                    _departCarFleetState.emit(DepartCarFleetState.OnFailedGetCurrentQueue(err))
                }
                .collect {
                    when (it) {
                        is GetCurrentQueueState.Success -> {
                            _departCarFleetState.emit(
                                DepartCarFleetState.SuccessGetCurrentQueue(
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
            _departCarFleetState.emit(
                DepartCarFleetState.DepartCarFleet(
                    _Car_fleetItem,
                    true,
                    queueNumber
                )
            )
        }
    }

    fun showQueueList(currentQueueNumber: String, locationId: Long, subLocationId: Long) {
        viewModelScope.launch {
            _departCarFleetState.emit(DepartCarFleetState.SelectQueueToDepartCar(_Car_fleetItem, currentQueueNumber, locationId, subLocationId))
        }
    }

    fun cancelDepart() {
        viewModelScope.launch {
            _departCarFleetState.emit(DepartCarFleetState.CancelDepartCar)
        }
    }
}