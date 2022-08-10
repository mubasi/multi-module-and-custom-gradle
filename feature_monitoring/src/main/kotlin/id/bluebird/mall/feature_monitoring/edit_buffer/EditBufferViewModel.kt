package id.bluebird.mall.feature_monitoring.edit_buffer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.mall.domain_location.domain.interactor.UpdateBuffer
import id.bluebird.mall.feature_monitoring.model.MonitoringModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class EditBufferViewModel(
    private val updateBuffer: UpdateBuffer
): ViewModel() {
    companion object {
        const val MINIMUM_BUFFER = 0
        private const val MAXIMUM_BUFFER = 99
    }

    private lateinit var monitoringModel: MonitoringModel
    private val _editBufferState = MutableSharedFlow<EditBufferState>()
    val editBufferState = _editBufferState.asSharedFlow()
    val buffer: MutableLiveData<String> = MutableLiveData("0")

    fun init(item: MonitoringModel) {
        monitoringModel = item
        buffer.value = monitoringModel.buffer.toString()
    }

    fun reduceBuffer() {
        if (getBufferValue() > MINIMUM_BUFFER)
            buffer.value = (getBufferValue() - 1).toString()
        viewModelScope.launch {
            _editBufferState.emit(EditBufferState.FocusState(false))
        }
    }

    fun addBuffer() {
        if (getBufferValue() < MAXIMUM_BUFFER)
            buffer.value = (getBufferValue() + 1).toString()

        viewModelScope.launch {
            _editBufferState.emit(EditBufferState.FocusState(false))
        }
    }

    fun closeDialog() {
        viewModelScope.launch {
            _editBufferState.emit(EditBufferState.ClosingDialog)
        }
    }

    fun saveBuffer() {
        if (!this::monitoringModel.isInitialized)
            return

        viewModelScope.launch {
            _editBufferState.emit(EditBufferState.OnProgressSave)
            updateBuffer
                .invoke(monitoringModel.subLocationId, getBufferValue())
                .flowOn(Dispatchers.Main)
                .catch {
                    _editBufferState.emit(EditBufferState.FailedSave)
                }
                .collect {
                    _editBufferState.emit(EditBufferState.SuccessSave)
                }
        }
    }

    private fun getBufferValue(): Int = buffer.value.orEmpty().toIntOrNull() ?: 0
}