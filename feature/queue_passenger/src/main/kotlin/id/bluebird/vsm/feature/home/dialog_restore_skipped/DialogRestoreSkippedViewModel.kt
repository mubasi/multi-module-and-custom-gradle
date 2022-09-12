package id.bluebird.vsm.feature.home.dialog_restore_skipped

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.domain.passenger.RestoreSkippedState
import id.bluebird.vsm.domain.passenger.domain.cases.RestoreSkipped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class DialogRestoreSkippedViewModel(
    private val restoreSkipped: RestoreSkipped
) : ViewModel() {

    companion object {
        const val ERROR_MESSAGE_UNKNOWN = "Unknown"
    }

    private val _dialogRestoreSkippedState: MutableSharedFlow<DialogRestoreSkippedState> =
        MutableSharedFlow()
    val dialogRestoreSkippedState = _dialogRestoreSkippedState.asSharedFlow()

    fun prosesRestore() {
        viewModelScope.launch {
            _dialogRestoreSkippedState.emit(DialogRestoreSkippedState.ProsesRestoreQueueSkipped)
        }
    }

    fun prosesRestoreQueue(
        number: String,
        queueId: Long,
        locationId: Long,
        subLocationId: Long
    ) {
        viewModelScope.launch {
            restoreSkipped.invoke(
                queueId,
                0,
                locationId,
                number,
                subLocationId,
                ""
            )
                .flowOn(Dispatchers.Main)
                .catch { cause ->
                    _dialogRestoreSkippedState.emit(
                        DialogRestoreSkippedState.FailedRestoreQueueSkipped(
                            message = cause.message ?: DialogRestoreSkippedViewModel.ERROR_MESSAGE_UNKNOWN
                        )
                    )
                }
                .collect {
                    when (it) {
                        is RestoreSkippedState.Success -> {
                            _dialogRestoreSkippedState.emit(
                                DialogRestoreSkippedState.SuccessRestoreQueueSkipped
                            )
                        }
                    }

                }
        }
    }

    fun restoreSkipped() {
        viewModelScope.launch {
            _dialogRestoreSkippedState.emit(DialogRestoreSkippedState.CancelRestoreQueueSkipped)
        }
    }

}