package id.bluebird.vsm.feature.queue_fleet.search_fleet

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.feature.queue_fleet.model.FleetItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SearchFleetViewModel : ViewModel() {
    val params: MutableLiveData<String> = MutableLiveData("")
    private val _fleetItems: MutableList<FleetItem> = mutableListOf()
    private val _searchState: MutableSharedFlow<SearchFleetState> = MutableSharedFlow()
    val searchState = _searchState.asSharedFlow()

    @VisibleForTesting
    fun setList(list: List<FleetItem>) {
        _fleetItems.addAll(list)
    }

    fun init(list: List<FleetItem>) {
        _fleetItems.clear()
        _fleetItems.addAll(list)
        filter()
    }

    fun filter() {
        viewModelScope.launch {
            val filterResult: MutableList<FleetItem> = mutableListOf()
            if (params.value != null) {
                _fleetItems.forEach {
                    if (it.name.contains(params.value ?: "")) {
                        filterResult.add(it)
                    }
                }
            } else {
                filterResult.addAll(_fleetItems)
            }
            _searchState.emit(SearchFleetState.UpdateFleetItems(filterResult))
        }
    }

    fun departFleet(fleetItem: FleetItem) {
        viewModelScope.launch {
            _searchState.emit(SearchFleetState.RequestDepartFleetItem(fleetItem))
        }
    }
}