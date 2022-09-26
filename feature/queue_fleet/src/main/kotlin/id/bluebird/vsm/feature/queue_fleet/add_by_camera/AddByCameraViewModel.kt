package id.bluebird.vsm.feature.queue_fleet.add_by_camera

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AddByCameraViewModel : ViewModel() {

    private val _addByCameraState : MutableSharedFlow<AddByCameraState> = MutableSharedFlow()
    val addByCameraState : SharedFlow<AddByCameraState> = _addByCameraState.asSharedFlow()
    val param: MutableLiveData<String> = MutableLiveData("")

    fun cancleScan() {
        viewModelScope.launch {
            _addByCameraState.emit(AddByCameraState.CancleScan)
        }
    }

    fun proseScan() {
        viewModelScope.launch {
            _addByCameraState.emit(AddByCameraState.ProsesScan(number = param.value.toString()))
        }
    }

    fun repeatTakePicture() {
        viewModelScope.launch {
            _addByCameraState.emit(AddByCameraState.RepeatTakePicture)
        }
    }

}