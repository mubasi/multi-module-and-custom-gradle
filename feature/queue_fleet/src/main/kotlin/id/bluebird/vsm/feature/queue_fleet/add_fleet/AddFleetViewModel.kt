package id.bluebird.vsm.feature.queue_fleet.add_fleet

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.api.client.util.DateTime
import id.bluebird.vsm.core.extensions.StringExtensions.convertCreateAtValue
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.fleet.SearchFleetState
import id.bluebird.vsm.domain.fleet.domain.cases.AddFleet
import id.bluebird.vsm.domain.fleet.domain.cases.SearchFleet
import id.bluebird.vsm.domain.passenger.WaitingQueueState
import id.bluebird.vsm.domain.passenger.domain.cases.SearchWaitingQueue
import id.bluebird.vsm.feature.queue_fleet.model.FleetItem
import id.bluebird.vsm.feature.select_location.LocationNavigationTemporary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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
    private var _locationId: Long = -1
    private var _isSearchQueue: Boolean = false
    @VisibleForTesting
    fun setParams(temp: String) {
        param.value = temp
    }
    @VisibleForTesting
    fun setSubLocation(temp: Long) {
        _subLocation = temp
    }
    @VisibleForTesting
    fun setIsSearchQueue(temp: Boolean) {
        _isSearchQueue = temp
    }

    @VisibleForTesting
    fun valIsSearchQueue() : Boolean {
        return _isSearchQueue
    }

    @VisibleForTesting
    fun valSubLocationId() : Long {
        return _subLocation
    }

    fun init(locationId: Long, subLocationId: Long, isSearchQueue: Boolean) {
        _isSearchQueue = isSearchQueue
        _locationId = locationId
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

    fun searchQueue() {
        viewModelScope.launch {
            resetValue()
            _addFleetState.emit(AddFleetState.GetListEmpty)
            delay(200)
            _addFleetState.emit(AddFleetState.OnProgressGetList)
            delay(200)
            searchWaitingQueue
                .invoke(param.value ?: "", _locationId, _subLocation)
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
                _addFleetState.emit(
                    AddFleetState.FinishSelectQueue(
                        selectedFleetNumber.value ?: ""
                    )
                )
            }
            return
        }
        viewModelScope.launch {
            addFleet.invoke(
                fleetNumber = selectedFleetNumber.value ?: "",
                subLocationId = _subLocation,
                locationId = LocationNavigationTemporary.getLocationNav()?.locationId
                    ?: UserUtils.getLocationId()
            )
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

    fun String.convertCreateAtValue(): String {
        val dateTime = DateTime.parseRfc3339(this)
        val sdf = SimpleDateFormat("dd MMM yyyy '.' HH:mm", Locale("id", "ID"))
        return sdf.format(dateTime.value)
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

    fun resultScan(number: String) {
        param.value = number
        searchFleet()
        updateSelectedFleetNumber(number, 0)
    }
}