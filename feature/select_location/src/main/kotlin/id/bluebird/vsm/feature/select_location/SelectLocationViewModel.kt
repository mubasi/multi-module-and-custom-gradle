package id.bluebird.vsm.feature.select_location

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.domain.location.GetLocationsWithSubState
import id.bluebird.vsm.domain.location.domain.interactor.GetLocationsWithSub
import id.bluebird.vsm.feature.select_location.model.LocationModel
import id.bluebird.vsm.feature.select_location.model.LocationNavigation
import id.bluebird.vsm.feature.select_location.model.SubLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SelectLocationViewModel(private val getLocationsWithSub: GetLocationsWithSub) : ViewModel() {

    private var _state: MutableSharedFlow<SelectLocationState> = MutableSharedFlow()
    val state: SharedFlow<SelectLocationState> = _state.asSharedFlow()
    val _locations: MutableList<LocationModel> = mutableListOf()
    var params: MutableLiveData<String> = MutableLiveData("")
    var locationNav : LocationNavigation? = null
    private var _isFleetMenu = false

    @VisibleForTesting
    fun setValLocation(value : ArrayList<LocationModel>) {
        _locations.addAll(value)
    }

    @VisibleForTesting
    fun setValFleetMenu(value : Boolean) {
        _isFleetMenu = value
    }

    fun init(isFleetMenu: Boolean) {
        _isFleetMenu = isFleetMenu
        viewModelScope.launch {
            LocationNavigationTemporary.removeTempData()
            _state.emit(SelectLocationState.OnProgressGetLocations)
            delay(500)
            getData()
        }
    }

    fun searchScreen() {
        viewModelScope.launch {
            if(_locations.isEmpty()) {
                _state.emit(SelectLocationState.EmptyLocation)
            } else {
                _state.emit(SelectLocationState.SearchLocation)
            }
        }
    }

    fun setFromSearch() {
        viewModelScope.launch {
            updateValNav()
            _state.emit(SelectLocationState.ToAssignFromSearach(isFleetMenu = _isFleetMenu))
        }
    }

    private fun getData() {
        viewModelScope.launch {
            getLocationsWithSub.invoke()
                .catch { cause ->
                    _state.emit(SelectLocationState.OnError(cause))
                }
                .flowOn(Dispatchers.Main)
                .collect { result ->
                    when (result) {
                        is GetLocationsWithSubState.Success -> {
                            _locations.clear()
                            result.list.forEach { subLocations ->
                                val subList: MutableList<SubLocation> = mutableListOf()
                                subLocations.list.forEach { subLocationResult ->
                                    subList.add(
                                        SubLocation(
                                            id = subLocationResult.id,
                                            name = subLocationResult.name,
                                            locationId = subLocations.locationId,
                                            locationName = subLocations.locationName
                                        )
                                    )
                                }
                                _locations.add(
                                    LocationModel(
                                        id = subLocations.locationId,
                                        name = subLocations.locationName,
                                        type = LocationModel.PARENT,
                                        list = subList
                                    )
                                )
                            }
                            _state.emit(SelectLocationState.GetLocationSuccess(_locations))
                        }
                    }
                }
        }
    }

    fun expandOrCollapseParent(item: LocationModel, position: Int) {
        viewModelScope.launch {
            _state.emit(SelectLocationState.OnItemClick(item, position))
        }
    }

    fun selectLocation(subLocation: SubLocation) {
        viewModelScope.launch {
            val tempLocationNav = LocationNavigation(
                locationId = subLocation.locationId,
                locationName = subLocation.locationName,
                subLocationId = subLocation.id,
                subLocationName = subLocation.name
            )
            locationNav = tempLocationNav
            updateValNav()
            _state.emit(SelectLocationState.ToAssign(isFleetMenu = _isFleetMenu))
        }
    }

    private fun updateValNav(){
        LocationNavigationTemporary.updateLocationNav(locationNav)
    }
}