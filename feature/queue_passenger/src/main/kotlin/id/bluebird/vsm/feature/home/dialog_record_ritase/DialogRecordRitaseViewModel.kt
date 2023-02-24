package id.bluebird.vsm.feature.home.dialog_record_ritase

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.domain.passenger.TakeQueueState
import id.bluebird.vsm.domain.passenger.domain.cases.TakeQueue
import id.bluebird.vsm.feature.home.model.CurrentQueueCache
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DialogRecordRitaseViewModel(
    private val takeQueue: TakeQueue
) : ViewModel() {
    companion object {
        const val EMPTY_STRING = ""
        const val RITASE = 5L
        const val SELECT_FLEET_NUMBER = "Pilih no. lambung"
    }

    private val _action: MutableSharedFlow<DialogRecordRitaseState> = MutableSharedFlow()
    val action = _action.asSharedFlow()
    var subLocationId: Long = -1
    var locationId: Long = -1
    val currentQueue: MutableLiveData<CurrentQueueCache> = MutableLiveData()
    var fleetNumber: String = EMPTY_STRING

    @VisibleForTesting
    fun setValLocationId(value: Long) {
        locationId = value
    }

    @VisibleForTesting
    fun setValSubLocationId(value: Long) {
        subLocationId = value
    }

    @VisibleForTesting
    fun setValFleetNumber(value: String) {
        fleetNumber = value
    }

    @VisibleForTesting
    fun setValCurrentQueue(value: CurrentQueueCache) {
        currentQueue.postValue(value)
    }

    fun init(
        queue: CurrentQueueCache,
        valLocationId: Long,
        valSubLocationId: Long,
        valFleetNumber: String
    ) {
        viewModelScope.launch {
            _action.emit(DialogRecordRitaseState.ProgressDialog)
            currentQueue.value = queue
            locationId = valLocationId
            subLocationId = valSubLocationId
            fleetNumber = valFleetNumber
            _action.emit(DialogRecordRitaseState.Idle)
        }
    }

    fun cancleDialog() {
        viewModelScope.launch {
            _action.emit(
                DialogRecordRitaseState.CancelDialog
            )
        }
    }

    fun selectFleet() {
        viewModelScope.launch {
            _action.emit(
                DialogRecordRitaseState.SelectFleet
            )
        }
    }

    fun prosesDialog() {
        viewModelScope.launch {
            if (fleetNumber.isEmpty()) {
                _action.emit(
                    DialogRecordRitaseState.FleetEmpty
                )
            } else {
                prosesRitase()
            }
        }
    }

    private suspend fun prosesRitase() {
        _action.emit(
            DialogRecordRitaseState.ProgressDialog
        )
        takeQueue.invoke(
            queueId = currentQueue.value?.id ?: -1,
            queueNumber = currentQueue.value?.number ?: "",
            queueType = RITASE,
            locationId = locationId,
            subLocationId = subLocationId,
            fleetNumber = fleetNumber
        )
            .catch { cause ->
                _action.emit(DialogRecordRitaseState.OnError(cause, fleetNumber))
            }
            .collect {
                when (it) {
                    is TakeQueueState.Success -> {
                        _action.emit(
                            DialogRecordRitaseState.SuccessRitase(
                                fleetNumber = fleetNumber,
                                queueNumber = currentQueue.value?.number ?: ""
                            )
                        )
                    }
                }
            }
    }

}