package id.bluebird.vsm.feature.select_location.search_mall_location

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.feature.select_location.model.CacheParentModel
import id.bluebird.vsm.feature.select_location.model.LocationModel
import id.bluebird.vsm.feature.select_location.model.SubLocation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SearchMallLocationViewModel : ViewModel() {

    private var _state: MutableSharedFlow<SearchMallLocationState> = MutableSharedFlow()
    val state: SharedFlow<SearchMallLocationState> = _state.asSharedFlow()
    val params: MutableLiveData<String> = MutableLiveData("")

    val locations: MutableList<LocationModel> = mutableListOf()

    fun init(parentList :  List<CacheParentModel>, childList : List<SubLocation>) {
        viewModelScope.launch {
            _state.emit(SearchMallLocationState.Init)

            parentList.forEach { result ->
                val tempSubLocation : ArrayList<SubLocation> = ArrayList()

                val listSubLocation = childList.filter {
                    it.locationId == result.id
                }

                tempSubLocation.addAll(listSubLocation)

                val tempLocation = LocationModel(
                    id = result.id,
                    name = result.name,
                    isExpanded = result.isExpanded,
                    type = result.type,
                    list = tempSubLocation.toList()
                )
                locations.add(tempLocation)
            }

            delay(1000)
            _state.emit(SearchMallLocationState.Idle(
                list = locations
            ))
        }
    }

    fun expandOrCollapseParent(item: LocationModel, position: Int) {
        viewModelScope.launch {
            _state.emit(SearchMallLocationState.OnItemClick(item, position))
        }
    }


    fun selectLocation(subLocation: SubLocation) {
        viewModelScope.launch {
            _state.emit(
                SearchMallLocationState.SelectLocation(
                    locationId = subLocation.locationId,
                    subLocationId = subLocation.id
                )
            )
        }
    }
}