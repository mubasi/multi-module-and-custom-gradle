package id.bluebird.vsm.feature.airport_fleet.add_by_camera

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.airport_assignment.StockDepartState
import id.bluebird.vsm.domain.airport_assignment.domain.cases.AddFleetAirport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddByCameraAirportViewModel(
    private val addFleetAirport: AddFleetAirport,
) : ViewModel() {

    companion object {
        const val EMPTY_STRING = ""
    }

    private val _addByCameraState : MutableSharedFlow<AddByCameraState> = MutableSharedFlow()
    val addByCameraState : SharedFlow<AddByCameraState> = _addByCameraState.asSharedFlow()
    val param: MutableLiveData<String> = MutableLiveData("")
    var subLocationId : MutableLiveData<Long> = MutableLiveData()

    fun init(tempSubLocationId : Long) {
        subLocationId.value = tempSubLocationId
    }

    fun cancleScan() {
        viewModelScope.launch {
            _addByCameraState.emit(AddByCameraState.CancleScan)
        }
    }

    fun proseScan() {
        viewModelScope.launch {
            val numberFleet = param.value?.filter { !it.isWhitespace() }
            addFleetAirport.invoke(UserUtils.getLocationId(), numberFleet ?: EMPTY_STRING, subLocationId.value ?: 0, isTu = false)
                .flowOn(Dispatchers.Main)
                .catch { cause ->
                    _addByCameraState.emit(AddByCameraState.OnError(cause))
                }.collect {
                    when(it) {
                        is StockDepartState.Success -> {
                            _addByCameraState.emit(AddByCameraState.ProsesScan(number = param.value.toString()))
                        }
                    }
                }
        }
    }

    fun repeatTakePicture() {
        viewModelScope.launch {
            _addByCameraState.emit(AddByCameraState.RepeatTakePicture)
        }
    }
}