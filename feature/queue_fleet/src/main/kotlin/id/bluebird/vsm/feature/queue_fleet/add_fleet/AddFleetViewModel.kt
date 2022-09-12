package id.bluebird.vsm.feature.queue_fleet.add_fleet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.core.extensions.StringExtensions.convertCreateAtValue
import id.bluebird.vsm.domain.fleet.SearchFleetState
import id.bluebird.vsm.domain.fleet.domain.cases.AddFleet
import id.bluebird.vsm.domain.fleet.domain.cases.SearchFleet
import id.bluebird.vsm.domain.passenger.WaitingQueueState
import id.bluebird.vsm.domain.passenger.domain.cases.SearchWaitingQueue
import id.bluebird.vsm.feature.queue_fleet.model.FleetItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddFleetViewModel(
    private val searchFleet: SearchFleet,
    private val addFleet: AddFleet,
    private val searchWaitingQueue: SearchWaitingQueue,
) : ViewModel() {

    private val _addFleetState: MutableSharedFlow<AddFleetState> = MutableSharedFlow()
    val addFleetState: SharedFlow<AddFleetState> = _addFleetState.asSharedFlow()
    val param: MutableLiveData<String> = MutableLiveData("")
    val selectedFleetNumber: MutableLiveData<String> = MutableLiveData("")
    private var _lastPosition: Int = -1
    private var _newPosition: Int = -1
    private var _subLocation: Long = -1
    private var _isSearchQueue: Boolean = false

    fun init(subLocationId: Long, isSearchQueue: Boolean) {
        _isSearchQueue = isSearchQueue
        _subLocation = subLocationId
        if (isSearchQueue) {
            searchQueue()
        } else {
            searchFleet()
        }
        viewModelScope.launch {
            _addFleetState.emit(AddFleetState.OnProgressGetList)
        }
    }

    private fun searchQueue() {
        viewModelScope.launch {
            resetValue()
            _addFleetState.emit(AddFleetState.GetListEmpty)
            delay(200)
            _addFleetState.emit(AddFleetState.OnProgressGetList)
            delay(200)
            searchWaitingQueue
                .invoke(param.value ?: "", _subLocation)
                .catch { e ->
                    _addFleetState.emit(AddFleetState.QueueSearchError(e))
                }
                .flowOn(Dispatchers.Main)
                .collect {
                    _addFleetState.emit(
                        when (it) {
                            is WaitingQueueState.EmptyResult -> AddFleetState.GetListEmpty
                            is WaitingQueueState.Success -> AddFleetState.SuccessGetQueue(it.waitingQueue.map { queue -> queue.number })
                        }
                    )
                }
        }
    }

    fun updateSelectedFleetNumber(fleetName: String, position: Int) {
        selectedFleetNumber.updateSelectedFleetNumberValue(fleetName)
        viewModelScope.launch {
            updatePosition(position)
            _addFleetState.emit(AddFleetState.UpdateSelectPosition(_lastPosition, _newPosition))
        }
    }

    private fun updatePosition(position: Int) {
        if (selectedFleetNumber.getSelectedFleetNumberValueIsEmpty()) {
            _lastPosition = position
            _newPosition = -1
        } else {
            _lastPosition = _newPosition
            _newPosition = position
        }
    }

    fun addFleet() {
        if (_isSearchQueue) {
            viewModelScope.launch {
                _addFleetState.emit(AddFleetState.FinishSelectQueue(selectedFleetNumber.value ?: ""))
            }
            return
        }
        viewModelScope.launch {
            addFleet.invoke(selectedFleetNumber.value ?: "", _subLocation)
                .flowOn(Dispatchers.Main)
                .catch { cause: Throwable ->
                    _addFleetState.emit(AddFleetState.AddError(err = cause))
                }
                .collect {
                    when (it) {
                        is id.bluebird.vsm.domain.fleet.AddFleetState.Success -> {
                            val fleetItem = FleetItem(
                                id = it.fleetItemResult.fleetId,
                                name = it.fleetItemResult.fleetName,
                                arriveAt = it.fleetItemResult.arriveAt.convertCreateAtValue()
                            )
                            _addFleetState.emit(AddFleetState.AddFleetSuccess(fleetItem))
                        }
                    }
                }
        }
    }

    fun searchFleet() {
        if (_isSearchQueue) {
            searchQueue()
            return
        }
        viewModelScope.launch {
            resetValue()
            _addFleetState.emit(AddFleetState.GetListEmpty)
            delay(200)
            _addFleetState.emit(AddFleetState.OnProgressGetList)
            searchFleet.invoke(param.value)
                .catch { cause: Throwable ->
                    _addFleetState.emit(AddFleetState.SearchError(err = cause))
                }
                .flowOn(Dispatchers.Main)
                .collect {
                    when (it) {
                        SearchFleetState.EmptyResult -> {
                            _addFleetState.emit(AddFleetState.GetListEmpty)
                        }
                        is SearchFleetState.Success -> {
                            _addFleetState.emit(AddFleetState.GetListSuccess(it.fleetNumbers))
                        }
                    }
                }
        }
    }

    private fun resetValue() {
        selectedFleetNumber.value = ""
        updatePosition(-1)
    }

    private fun LiveData<String>.getSelectedFleetNumberValueIsEmpty(): Boolean =
        (this.value ?: "").isBlank()

    private fun MutableLiveData<String>.updateSelectedFleetNumberValue(fleetName: String) {
        if (this.getSelectedFleetNumberValueIsEmpty().or(this.value != fleetName)) {
            this.value = fleetName
        } else {
            this.value = ""
        }
    }
}