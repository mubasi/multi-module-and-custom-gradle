package id.bluebird.mall.home.dialog_delete_skipped

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.mall.domain_pasenger.DeleteSkippedState
import id.bluebird.mall.domain_pasenger.domain.cases.DeleteSkipped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class DialogDeleteSkippedViewModel(
    private val deleteSkipped: DeleteSkipped
): ViewModel() {

    companion object {
        const val ERROR_MESSAGE_UNKNOWN = "Unknown"
    }

    private val _dialogDeleteSkippedState: MutableSharedFlow<DialogDeleteSkippedState> =
        MutableSharedFlow()
    val dialogDeleteSkippedState = _dialogDeleteSkippedState.asSharedFlow()

    fun prosesDelete() {
        viewModelScope.launch {
            _dialogDeleteSkippedState.emit(DialogDeleteSkippedState.ProsesDeleteQueueSkipped)
        }
    }

    fun prosesDeleteQueue(
        number: String,
        queueId: Long,
        locationId: Long,
        subLocationId: Long
    ) {
        viewModelScope.launch {
            deleteSkipped.invoke(
                queueId,
                6,
                locationId,
                number,
                subLocationId,
                ""
            )
                .flowOn(Dispatchers.Main)
                .catch { cause ->
                    _dialogDeleteSkippedState.emit(
                        DialogDeleteSkippedState.FailedDeleteQueueSkipped(
                            message = cause.message ?: DialogDeleteSkippedViewModel.ERROR_MESSAGE_UNKNOWN
                        )
                    )
                }
                .collect {
                    when (it) {
                        is DeleteSkippedState.Success -> {
                            _dialogDeleteSkippedState.emit(
                                DialogDeleteSkippedState.SuccessDeleteQueueSkipped
                            )
                        }
                    }

                }
        }
    }

    fun skipDelete() {
        viewModelScope.launch {
            _dialogDeleteSkippedState.emit(DialogDeleteSkippedState.CancelDeleteQueueSkipped)
        }
    }

}