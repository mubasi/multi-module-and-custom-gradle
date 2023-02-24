package id.bluebird.vsm.feature.home.ritase_fleet

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.core.extensions.StringExtensions.convertCreateAtValue
import id.bluebird.vsm.domain.fleet.GetListFleetState
import id.bluebird.vsm.domain.fleet.domain.cases.GetListFleet
import id.bluebird.vsm.domain.fleet.model.FleetItemResult
import id.bluebird.vsm.feature.home.model.FleetItemList
import id.bluebird.vsm.feature.home.model.UserInfo
import id.bluebird.vsm.feature.select_location.LocationNavigationTemporary
import id.bluebird.vsm.feature.select_location.model.LocationNavigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class RitaseFleetViewModel(
    private val getFleet: GetListFleet,
) : ViewModel() {
    private val _ritaseFleetState: MutableSharedFlow<RitaseFleetState> =
        MutableSharedFlow()
    val ritaseFleetState = _ritaseFleetState.asSharedFlow()
    var currentLocation : LocationNavigation? = null
    val fleetItems: MutableList<FleetItemList> = mutableListOf()
    val params: MutableLiveData<String> = MutableLiveData("")
    var fleetItem : MutableLiveData<FleetItemList> = MutableLiveData(null)
    val selectedFleetNumber: MutableLiveData<String> = MutableLiveData("")
    var mUserInfo : UserInfo = UserInfo()
    private var _lastPosition: Int = -1
    private var _newPosition: Int = -1

    @VisibleForTesting
    fun getValLastPosition() : Int {
        return _lastPosition
    }

    @VisibleForTesting
    fun getValNewtPosition() : Int {
        return _newPosition
    }

    fun init(){
        currentLocation = LocationNavigationTemporary.getLocationNav()
        initLocation()
    }

    private fun initLocation() {
        viewModelScope.launch {
            if(currentLocation == null) {
                _ritaseFleetState.emit(RitaseFleetState.CurrentQueueNotFound)
            } else {
                if (currentLocation!!.locationId!! > 0 && currentLocation!!.subLocationId!! > 0) {
                    mUserInfo.subLocationId = currentLocation!!.subLocationId!!
                    mUserInfo.locationId = currentLocation!!.locationId!!
                    getFleetList()
                }
            }
        }
    }
    fun updateSelectedFleetNumber(fleetNumber : String, position : Int) {
        selectedFleetNumber.updateSelectedFleetNumberValue(fleetNumber)
        viewModelScope.launch {
            updatePosition(position)
            _ritaseFleetState.emit(RitaseFleetState.UpdateSelectPosition(_lastPosition, _newPosition))
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

    fun clearSelected() {
        selectedFleetNumber.value = ""
        fleetItem.value = null
    }

    fun saveFleet(){
        viewModelScope.launch {
           val resultState = if(selectedFleetNumber.getSelectedFleetNumberValueIsEmpty()) {
               RitaseFleetState.FleetNotSelected
            } else {
               RitaseFleetState.SuccessSaveFleet(
                   fleetNumber = selectedFleetNumber.value ?: ""
               )
            }
            _ritaseFleetState.emit(
                resultState
            )
        }
    }

    fun filterFleet() {
        viewModelScope.launch {
            val resultState = if (resultFilterFleet().isEmpty()) {
                RitaseFleetState.FilterFleetFailed
            } else {
                RitaseFleetState.FilterFleet(resultFilterFleet())
            }
            _ritaseFleetState.emit(
                resultState
            )
        }
    }

    private fun resultFilterFleet() : ArrayList<FleetItemList> {
        val filteredlist: ArrayList<FleetItemList> = ArrayList()
        for (item in fleetItems) {
            if (item.name.toLowerCase().contains(params.value!!.toLowerCase())) {
                filteredlist.add(item)
            }
        }
        return filteredlist
    }


    private fun getFleetList() {
        viewModelScope.launch {
            _ritaseFleetState.emit(RitaseFleetState.ProsesListFleet)
            if (fleetItems.isNotEmpty()) {
                _ritaseFleetState.emit(RitaseFleetState.GetListSuccess(fleetItems))
            } else {
                getFleet.invoke(mUserInfo.subLocationId)
                    .flowOn(Dispatchers.Main)
                    .catch { cause: Throwable ->
                        _ritaseFleetState.emit(RitaseFleetState.FailedGetList(cause))
                    }
                    .collect {
                        when (it) {
                            GetListFleetState.EmptyResult -> {
                                _ritaseFleetState.emit(RitaseFleetState.GetListEmpty)
                            }
                            is GetListFleetState.Success -> {
                                val fleetItemResults = setFleetItems(it.list)
                                fleetItems.addAll(fleetItemResults)
                                _ritaseFleetState.emit(RitaseFleetState.GetListSuccess(fleetItems))
                            }
                        }
                    }
            }
        }
    }

    private fun setFleetItems(items : List<FleetItemResult>) : ArrayList<FleetItemList> {
        val result : ArrayList<FleetItemList> = ArrayList()
        items.forEach { item ->
            result.add(
                FleetItemList(
                    id = item.fleetId,
                    name = item.fleetName,
                    arriveAt = item.arriveAt.convertCreateAtValue()
                )
            )
        }
        return result
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