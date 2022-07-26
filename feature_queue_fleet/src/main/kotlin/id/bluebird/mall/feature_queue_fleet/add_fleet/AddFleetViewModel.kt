package id.bluebird.mall.feature_queue_fleet.add_fleet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.mall.core.extensions.StringExtensions.convertCreateAtValue
import id.bluebird.mall.domain_fleet.SearchFleetState
import id.bluebird.mall.domain_fleet.domain.cases.AddFleet
import id.bluebird.mall.domain_fleet.domain.cases.SearchFleet
import id.bluebird.mall.feature_queue_fleet.model.FleetItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddFleetViewModel(
    private val searchFleet: SearchFleet,
    private val addFleet: AddFleet
) : ViewModel() {

    private val _addFleetState: MutableSharedFlow<AddFleetState> = MutableSharedFlow()
    val addFleetState: SharedFlow<AddFleetState> = _addFleetState.asSharedFlow()
    val param: MutableLiveData<String> = MutableLiveData("")
    val selectedFleetNumber: MutableLiveData<String> = MutableLiveData("")
    private var _lastPosition: Int = -1
    private var _newPosition: Int = -1
    private var _subLocation: Long = -1

    fun init(subLocationId: Long) {
        _subLocation = subLocationId
        searchFleet()
        viewModelScope.launch {
            _addFleetState.emit(AddFleetState.OnProgressGetList)
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
        viewModelScope.launch {
            addFleet.invoke(selectedFleetNumber.value ?: "", _subLocation)
                .flowOn(Dispatchers.Main)
                .catch { cause: Throwable ->
                    _addFleetState.emit(AddFleetState.AddError(err = cause))
                }
                .collect {
                    when (it) {
                        is id.bluebird.mall.domain_fleet.AddFleetState.Success -> {
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