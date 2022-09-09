package id.bluebird.mall.feature.select_location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.mall.domain_location.GetLocationsWithSubState
import id.bluebird.mall.domain_location.domain.interactor.GetLocationsWithSub
import id.bluebird.mall.feature.select_location.model.LocationModel
import id.bluebird.mall.feature.select_location.model.LocationNavigation
import id.bluebird.mall.feature.select_location.model.SubLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SelectLocationViewModel(private val getLocationsWithSub: GetLocationsWithSub) : ViewModel() {

    private var _state: MutableSharedFlow<SelectLocationState> = MutableSharedFlow()
    val state: SharedFlow<SelectLocationState> = _state.asSharedFlow()
    private val _locations: MutableList<LocationModel> = mutableListOf()
    private var _isFleetMenu = false

    fun init(isFleetMenu: Boolean) {
        _isFleetMenu = isFleetMenu
        viewModelScope.launch {
            LocationNavigationTemporary.removeTempData()
            _state.emit(SelectLocationState.OnProgressGetLocations)
            delay(500)
            getData()
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
                            result.list.forEach { map ->
                                val subList: MutableList<SubLocation> = mutableListOf()
                                map.value.list.forEach { subLocationResult ->
                                    subList.add(
                                        SubLocation(
                                            id = subLocationResult.id,
                                            name = subLocationResult.name,
                                            locationId = map.value.locationId,
                                            locationName = map.value.locationName
                                        )
                                    )
                                }
                                _locations.add(
                                    LocationModel(
                                        id = map.value.locationId,
                                        name = map.value.locationName,
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
            val locationNav = LocationNavigation(
                locationId = subLocation.locationId,
                locationName = subLocation.locationName,
                subLocationId = subLocation.id,
                subLocationName = subLocation.name
            )
            LocationNavigationTemporary.updateLocationNav(locationNav)
            _state.emit(SelectLocationState.ToAssign(isFleetMenu = _isFleetMenu))
        }
    }
}