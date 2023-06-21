package id.bluebird.vsm.feature.monitoring.filter_dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.feature.monitoring.main.MonitoringViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class FilterDialogViewModel : ViewModel() {

    private val _state: MutableSharedFlow<FilterDialogState> = MutableSharedFlow()
    val state = _state.asSharedFlow()

    fun setStatusFilter(result : MonitoringViewModel.FilterStatus) {
        viewModelScope.launch {
            _state.emit(
                FilterDialogState.SaveFilter(
                    result
                )
            )
        }
    }

    fun closeDialog() {
        viewModelScope.launch {
            _state.emit(
                FilterDialogState.CloseFilter
            )
        }
    }

}