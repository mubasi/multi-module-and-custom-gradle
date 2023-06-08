package id.bluebird.vsm.feature.select_location

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.airport_location.GetListSublocationAirportState
import id.bluebird.vsm.domain.airport_location.domain.cases.GetListSublocationAirport
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
    private val getLocationAirport: GetListSublocationAirport
) : ViewModel() {

    companion object {
        const val ENABLE_VALUE_GREATER = 0
        const val EMPTY_STRING = ""
    }

    private var _state: MutableSharedFlow<SelectLocationState> = MutableSharedFlow()
    val state: SharedFlow<SelectLocationState> = _state.asSharedFlow()
    val _locations: MutableList<LocationModel> = mutableListOf()
    val locationsAirport: MutableList<SubLocationModelCache> = mutableListOf()
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
            if (checkFilter()) {
                _state.emit(SelectLocationState.ErrorFilter)
            } else {
                _state.emit(
                    if(UserUtils.getIsUserAirport()) SelectLocationState.FilterLocationAirport(resultFilterLocationAirport())  else SelectLocationState.FilterFleet(resultFilterFleet())
                )
            }
        }
    }

    private fun checkFilter() : Boolean {
        return if(UserUtils.getIsUserAirport()) resultFilterLocationAirport().isEmpty() else resultFilterFleet().isEmpty()
    }

    private fun resultFilterFleet(): ArrayList<LocationModel> {
        val filteredlist: ArrayList<LocationModel> = ArrayList()
        for (item in _locations) {
            if (item.name.toLowerCase().contains((params.value?: EMPTY_STRING ).toLowerCase())) {
                filteredlist.add(item)
            }
        }
        return filteredlist
    }

    private fun resultFilterLocationAirport() : ArrayList<SubLocationModelCache> {
        val filteredlist: ArrayList<SubLocationModelCache> = ArrayList()
        for (item in locationsAirport) {
            if (item.name.toLowerCase().contains((params.value?: EMPTY_STRING ).toLowerCase())) {
                filteredlist.add(item)
            }
        }
        return filteredlist
    }

    fun searchScreen() {
        viewModelScope.launch {
            if (checkSubLocation()) {
                _state.emit(SelectLocationState.EmptyLocation)
            } else {
                _state.emit(SelectLocationState.SearchLocation(
                    UserUtils.getIsUserAirport()
                ))
            }
        }
    }

    private fun checkSubLocation() : Boolean {
        return if(UserUtils.getIsUserAirport()) {
            locationsAirport.isEmpty()
        } else {
            _locations.isEmpty()
        }
    }

    fun setFromSearch(isAirport : Boolean) {
        viewModelScope.launch {
            _state.emit(
                if(isAirport) SelectLocationState.ToAssignFromSearchAirport else
                SelectLocationState.ToAssignFromSearch(isFleetMenu = _isFleetMenu)
            )
        }
    }


    private fun getAirportSubLocation() {
        viewModelScope.launch {
            getLocationAirport.invoke(
                UserUtils.getLocationId(),
                showDeposition = true,
                showWingsChild = true
            )
                .catch { cause ->
                    _state.emit(SelectLocationState.OnError(cause))
                }
                .collect {
                    when (it) {
                        is GetListSublocationAirportState.Success -> {
                            locationsAirport.clear()
                            val list: ArrayList<SubLocationModelCache> = ArrayList()
                            it.result.subLocationList.map { item ->
                                list.add(
                                    SubLocationModelCache(
                                        id = item.subLocationId,
                                        name = item.subLocationName,
                                        isPerimeter = item.isDeposition,
                                        isWing = item.isWings,
                                        prefix = EMPTY_STRING
                                    )
                                )
                            }
                            locationsAirport.addAll(list)
                            _state.emit(SelectLocationState.GetSubLocationSuccess(locationsAirport))
                        }
                        GetListSublocationAirportState.EmptyResult -> {
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
                                            depositionId = subLocationResult.depositionId,
                                            haveDeposition = subLocationResult.haveDeposition,
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