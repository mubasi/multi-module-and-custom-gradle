package id.bluebird.vsm.feature.airport_fleet.add_fleet

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.airport_assignment.StockDepartState
import id.bluebird.vsm.domain.airport_assignment.domain.cases.AddFleetAirport
import id.bluebird.vsm.domain.fleet.SearchFleetState
import id.bluebird.vsm.domain.fleet.domain.cases.SearchFleet
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class AddFleetViewModelNonApsh(
    private val addFleetAirport: AddFleetAirport,
    private val searchFleet: SearchFleet
) : ViewModel() {
    companion object {
        const val MSG_ERROR_NUMBER_FLEET = "Nomor Mobil Kurang dari 5 karakter"
        const val EMPTY_SEARCH = ""
    }

    var param: MutableLiveData<String> = MutableLiveData()

    private val _addFleetState: MutableSharedFlow<AddFleetState> = MutableSharedFlow()
    val addFleetState: SharedFlow<AddFleetState> = _addFleetState.asSharedFlow()
    var isShowingTu: Boolean = false
    private var _isPerimeter = false
    private var _subLocationId = -1L

    @VisibleForTesting
    fun setParam(qSearch : String?) {
        param.value = qSearch
    }

    @VisibleForTesting
    fun getIsPerimeter() : Boolean {
        return _isPerimeter
    }

    @VisibleForTesting
    fun getSubLocationId() : Long {
        return _subLocationId
    }


    fun init(isPerimeter: Boolean, subLocationId: Long) {
        this._isPerimeter = isPerimeter
        this._subLocationId = subLocationId
        param.value = ""
        searchFleet()
    }

    fun addFleet(fleetNumber: String) {
        viewModelScope.launch {
            _addFleetState.emit(
                AddFleetState.ShowDialogAddFleet(fleetNumber)
            )
        }
    }

    fun addFleetFromButton(fleetNumber: String, isTu : Boolean) {
        viewModelScope.launch {
            if (fleetNumber.length < 5)
                _addFleetState.emit(
                    AddFleetState.AddFleetError(Throwable(MSG_ERROR_NUMBER_FLEET))
                )
            else {
                addFleetToStock(fleetNumber, isTu)
            }
        }
    }

    private suspend fun addFleetToStock(fleetNumber: String, isTu: Boolean) {
        addFleetAirport.invoke(
            locationId = UserUtils.getLocationId(),
            fleetNumber = fleetNumber,
            subLocation = _subLocationId,
            isTu = isTu
        )
            .flowOn(Dispatchers.Main)
            .catch { cause ->
                _addFleetState.emit(AddFleetState.AddFleetError(cause))
            }.collect {
                when (it) {
                    is StockDepartState.Success -> {
                        _addFleetState.emit(
                            AddFleetState.AddFleetSuccess(
                                fleetNumber = fleetNumber
                            )
                        )
                    }
                }
            }
    }

    fun filterFleet(qSearch: String) {
        viewModelScope.launch {
            param.value = qSearch
            searchFleet()
        }
    }

    fun searchFleet() {
        viewModelScope.launch {
            _addFleetState.emit(AddFleetState.ProgressSearch)
            delay(350L)
            _addFleetState.emit(AddFleetState.FleetsReset)
            searchFleet.invoke(param = param.value ?: EMPTY_SEARCH)
                .catch { cause ->
                    _addFleetState.emit(AddFleetState.SearchFleetError(cause))
                }
                .collect {
                    when (it) {
                        is SearchFleetState.Success -> {
                            val fleetNames: MutableList<String> = mutableListOf()
                            it.fleetNumbers.forEach { item ->
                                fleetNames.add(item)
                            }
                            _addFleetState.emit(AddFleetState.SearchFleetSuccess(fleetNames))
                        }
                    }
                }
        }
    }
}