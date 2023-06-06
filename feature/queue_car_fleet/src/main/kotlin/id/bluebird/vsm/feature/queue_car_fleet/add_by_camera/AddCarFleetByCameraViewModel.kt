package id.bluebird.vsm.feature.queue_car_fleet.add_by_camera

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AddCarFleetByCameraViewModel : ViewModel() {

    private val _addCarFleetByCameraState : MutableSharedFlow<AddCarFleetByCameraState> = MutableSharedFlow()
    val addCarFleetByCameraState : SharedFlow<AddCarFleetByCameraState> = _addCarFleetByCameraState.asSharedFlow()
    val param: MutableLiveData<String> = MutableLiveData("")

    fun cancleScan() {
        viewModelScope.launch {
            _addCarFleetByCameraState.emit(AddCarFleetByCameraState.CancleScan)
        }
    }

    fun proseScan() {
        viewModelScope.launch {
            _addCarFleetByCameraState.emit(AddCarFleetByCameraState.ProsesScan(number = param.value.toString()))
        }
    }

    fun repeatTakePicture() {
        viewModelScope.launch {
            _addCarFleetByCameraState.emit(AddCarFleetByCameraState.RepeatTakePicture)
        }
    }

}