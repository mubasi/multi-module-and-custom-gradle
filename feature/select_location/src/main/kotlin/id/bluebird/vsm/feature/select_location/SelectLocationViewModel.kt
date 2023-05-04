package id.bluebird.vsm.feature.select_location

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.location.GetLocationsWithSubState
import id.bluebird.vsm.domain.location.domain.interactor.GetLocationsWithSub
import id.bluebird.vsm.domain.user.GetUserAssignmentState
import id.bluebird.vsm.domain.user.domain.intercator.GetUserAssignment
import id.bluebird.vsm.feature.select_location.model.LocationModel
import id.bluebird.vsm.feature.select_location.model.LocationNavigation
import id.bluebird.vsm.feature.select_location.model.SubLocation
import id.bluebird.vsm.feature.select_location.model.SubLocationModelCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SelectLocationViewModel(
    private val getLocationsWithSub: GetLocationsWithSub,
    private val getUserAssignment: GetUserAssignment
) : ViewModel() {

    companion object {
        const val ENABLE_VALUE_GREATER = 0
    }

    private var _state: MutableSharedFlow<SelectLocationState> = MutableSharedFlow()
    val state: SharedFlow<SelectLocationState> = _state.asSharedFlow()
    val _locations: MutableList<LocationModel> = mutableListOf()
    var params: MutableLiveData<String> = MutableLiveData("")
    var locationNav: LocationNavigation? = null
    private var _isFleetMenu = false

    @VisibleForTesting
    fun setValLocation(value : ArrayList<LocationModel>) {
        _locations.addAll(value)
    }

    @VisibleForTesting
    fun setValFleetMenu(value: Boolean) {
        _isFleetMenu = value
    }

    fun init(isFleetMenu: Boolean) {
        _isFleetMenu = isFleetMenu
        viewModelScope.launch {
            LocationNavigationTemporary.removeTempData()
            _state.emit(SelectLocationState.OnProgressGetLocations)
            delay(500)
            initRcvByUserType()
        }
    }

    private suspend fun initRcvByUserType() {
        if (UserUtils.getIsUserAirport()) {
            _state.emit(SelectLocationState.UserAirport)
            getAirportSubLocation()
        } else {
            _state.emit(SelectLocationState.UserOutlet)
            getOutletLocation()
        }
    }


    fun filterFleet() {
        viewModelScope.launch {
            if (resultFilterFleet().isEmpty()) {
                _state.emit(SelectLocationState.ErrorFilter)
            } else {
                _state.emit(SelectLocationState.FilterFleet(resultFilterFleet()))
            }
        }
    }

    private fun resultFilterFleet(): ArrayList<LocationModel> {
        val filteredlist: ArrayList<LocationModel> = ArrayList()
        for (item in _locations) {
            if (item.name.toLowerCase().contains(params.value!!.toLowerCase())) {
                filteredlist.add(item)
            }
        }
        return filteredlist
    }

    fun searchScreen() {
        viewModelScope.launch {
            if (_locations.isEmpty()) {
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

    private fun getAirportSubLocation() {
        viewModelScope.launch {
            getUserAssignment.invoke(
                UserUtils.getUserId()
            )
                .catch { cause ->
                    _state.emit(SelectLocationState.OnError(cause))
                }
                .collect {
                    when (it) {
                        is GetUserAssignmentState.Success -> {
                            val list: ArrayList<SubLocationModelCache> = ArrayList()
                            it.result.forEach { item ->
                                list.add(
                                    SubLocationModelCache(
                                        id = item.subLocationId,
                                        name = item.subLocationName,
                                        locationId = item.locationId,
                                        locationName = item.locationName,
                                        isPerimeter = item.isDeposition,
                                        isWing = item.isWings,
                                        prefix = item.prefix
                                    )
                                )
                            }
                            _state.emit(SelectLocationState.GetSubLocationSuccess(list))
                        }
                        GetUserAssignmentState.UserNotFound -> {
                            _state.emit(SelectLocationState.EmptyLocation)
                        }
                    }
                }
        }
    }


    private fun getOutletLocation() {
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
                                            locationName = subLocations.locationName,
                                            prefix = subLocationResult.prefix
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
                subLocationName = subLocation.name,
                prefix = subLocation.prefix
            )
            locationNav = tempLocationNav
            updateValNav()
            _state.emit(SelectLocationState.ToAssign(isFleetMenu = _isFleetMenu))
        }
    }

    fun selectLocationAirport(subLocation: SubLocationModelCache) {
        viewModelScope.launch {
            val tempLocationNav = LocationNavigation(
                locationId = subLocation.locationId,
                locationName = subLocation.locationName,
                subLocationId = subLocation.id,
                subLocationName = subLocation.name,
                isPerimeter = subLocation.isPerimeter,
                isWing = subLocation.isWing,
                prefix = subLocation.prefix
            )
            locationNav = tempLocationNav
            updateValNav()
            _state.emit(SelectLocationState.ToAssignAirport)
        }
    }

    private fun updateValNav() {
        LocationNavigationTemporary.updateLocationNav(locationNav)
    }

    fun clearSearch() {
        params.value = ""
        filterFleet()
    }
}