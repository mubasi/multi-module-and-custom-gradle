package id.bluebird.vsm.feature.queue_car_fleet.deposition_fleet

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.core.extensions.StringExtensions.convertCreateAtValue
import id.bluebird.vsm.domain.fleet.GetListFleetState
import id.bluebird.vsm.domain.fleet.domain.cases.GetListFleet
import id.bluebird.vsm.feature.queue_car_fleet.model.CarFleetItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DepositionFleetViewModel(
    private val _getFleet: GetListFleet,
) : ViewModel() {

    companion object {
        const val DEFAULT_TITLE = "..."
    }

    private val _actionState: MutableSharedFlow<DepositionFleetState> =
        MutableSharedFlow()
    val actionState: SharedFlow<DepositionFleetState> = _actionState.asSharedFlow()
    private val listFleets: MutableList<CarFleetItem> = mutableListOf()
    private val _location : MutableLiveData<String> = MutableLiveData(DEFAULT_TITLE)
    val location : LiveData<String> = _location
    var depositionStock : Long = 0L
    var subLocationId : Long = -1

    @VisibleForTesting
    fun setListFleet(list : List<CarFleetItem>) {
        listFleets.addAll(list)
    }

    fun init(idSubLocation : Long, stockDeposition : Long, title : String) {
        subLocationId = idSubLocation
        depositionStock = stockDeposition
        _location.postValue(title)
        getFleetList()
    }

    private fun getFleetList() {
        viewModelScope.launch {
            _actionState.emit(DepositionFleetState.ProgressGetList)
            if (listFleets.isNotEmpty()) {
                _actionState.emit(DepositionFleetState.GetListSuccess(listFleets.toList()))
            } else {
                _getFleet.invoke(subLocationId)
                    .catch { cause: Throwable ->
                        _actionState.emit(DepositionFleetState.FailedGetList(cause))
                    }
                    .collect {
                        when (it) {
                            GetListFleetState.EmptyResult -> {
                                _actionState.emit(DepositionFleetState.GetListEmpty)
                            }
                            is GetListFleetState.Success -> {
                                listFleets.clear()
                                it.list.forEach { item ->
                                    listFleets.add(
                                        CarFleetItem(
                                            id = item.fleetId,
                                            name = item.fleetName,
                                            arriveAt = item.arriveAt.convertCreateAtValue()
                                        )
                                    )
                                }
                                _actionState.emit(
                                    DepositionFleetState.GetListSuccess(
                                        listFleets.toList()
                                    )
                                )
                            }
                        }
                    }
            }
        }
    }
}