package id.bluebird.mall.feature_user_management.search_location

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.mall.domain_location.LocationDomainState
import id.bluebird.mall.domain_location.domain.interactor.GetLocations
import id.bluebird.mall.feature_user_management.search_location.model.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchLocationViewModel(
    private val getLocations: GetLocations
) : ViewModel() {

    private val _searchLocationState: MutableSharedFlow<SearchLocationState> = MutableSharedFlow()
    val searchState: SharedFlow<SearchLocationState> = _searchLocationState.asSharedFlow()
    private var locations: List<Location> = listOf()
    private var _newSelectedPosition = -1
    private var _lastSelectedPosition = -1
    var searchKey = MutableLiveData("")
    val selectedLocation = MutableLiveData<Location?>(null)

    fun init() {
        viewModelScope.launch {
            _searchLocationState.emit(SearchLocationState.OnProgressGetList)
            getLocations
                .invoke()
                .flowOn(Dispatchers.Main)
                .catch {
                    _searchLocationState.emit(SearchLocationState.FailedGetList)
                }
                .collect {
                    when (it) {
                        is LocationDomainState.Success -> {
                            locations = it.value.map { data ->
                                Location(data.id, data.locationName)
                            }
                            _searchLocationState.emit(SearchLocationState.Success(locations))
                        }
                        is LocationDomainState.Empty -> {
                            _searchLocationState.emit(SearchLocationState.EmptyList)
                        }
                        else -> {}
                    }
                }
        }
    }

    fun filter() {
        val result = locations.filter { it.name.contains(getSearchKeyword(), true) }
        result.forEach { it.isSelected = false }
        selectedLocation.value = null
        viewModelScope.launch {
            _searchLocationState.emit(SearchLocationState.Success(result))
        }
    }

    fun clearFilter() {
        searchKey.value = ""
        filter()
    }

    private fun getSearchKeyword(): String = searchKey.value ?: ""

    fun updateSelectedItem(item: Location, position: Int) {
        selectedLocation.value = item
        viewModelScope.launch {
            updateLocation(position)
            _searchLocationState.emit(
                SearchLocationState.UpdateSelectedLocation(
                    _newSelectedPosition,
                    _lastSelectedPosition,
                    selectedLocation.value
                )
            )
        }
    }

    private fun updateLocation(position: Int) {
        if (selectedLocation.value != null) {
            _lastSelectedPosition = _newSelectedPosition
            _newSelectedPosition = position
        } else {
            _lastSelectedPosition = position
            _newSelectedPosition = -1
        }
    }

    fun chooseLocation() {
        if (selectedLocation.value == null)
            return
        viewModelScope.launch {
            selectedLocation.value?.let { location ->
                _searchLocationState.emit(SearchLocationState.SetSelected(location))
            }
        }
    }
}