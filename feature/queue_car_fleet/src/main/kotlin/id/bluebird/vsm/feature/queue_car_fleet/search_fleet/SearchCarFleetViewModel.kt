package id.bluebird.vsm.feature.queue_car_fleet.search_fleet

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.feature.queue_car_fleet.model.CarFleetItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SearchCarFleetViewModel : ViewModel() {
    val params: MutableLiveData<String> = MutableLiveData("")
    private val _Car_fleetItems: MutableList<CarFleetItem> = mutableListOf()
    private val _searchState: MutableSharedFlow<SearchCarFleetState> = MutableSharedFlow()
    val searchState = _searchState.asSharedFlow()

    @VisibleForTesting
    fun setList(list: List<CarFleetItem>) {
        _Car_fleetItems.addAll(list)
    }

    fun init(list: List<CarFleetItem>) {
        _Car_fleetItems.clear()
        _Car_fleetItems.addAll(list)
        filter()
    }

    fun filter() {
        viewModelScope.launch {
            val filterResult: MutableList<CarFleetItem> = mutableListOf()
            if (params.value != null) {
                _Car_fleetItems.forEach {
                    if (it.name.contains(params.value ?: "")) {
                        filterResult.add(it)
                    }
                }
            } else {
                filterResult.addAll(_Car_fleetItems)
            }
            _searchState.emit(SearchCarFleetState.UpdateCarFleetItems(filterResult))
        }
    }

    fun departFleet(carFleetItem: CarFleetItem) {
        viewModelScope.launch {
            _searchState.emit(SearchCarFleetState.RequestDepartCarFleetItem(carFleetItem))
        }
    }
}