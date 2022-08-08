package id.bluebird.mall.home.dialog_skip_queue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.mall.domain_pasenger.SkipQueueState
import id.bluebird.mall.domain_pasenger.domain.cases.SkipQueue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class DialogSkipQueueViewModel(
    private val skipQueue: SkipQueue
) : ViewModel() {

    companion object {
        const val ERROR_MESSAGE_UNKNOWN = "Unknown"
    }

    private val _dialogSkipQueueState: MutableSharedFlow<DialogSkipQueueState> =
        MutableSharedFlow()
    val dialogSkipQueueState = _dialogSkipQueueState.asSharedFlow()

    fun cancelSkipQueue() {
        viewModelScope.launch {
            _dialogSkipQueueState.emit(
                DialogSkipQueueState.CancleDialog
            )
        }
    }

    fun prosesDialog() {
        viewModelScope.launch {
            _dialogSkipQueueState.emit(
                DialogSkipQueueState.ProsessDialog
            )
        }
    }

    fun prosesSkipQueue(
        queueId: Long,
        locationId: Long,
        subLocationId: Long
    ) {
        viewModelScope.launch {

            skipQueue.invoke(
                queueId = queueId,
                locationId = locationId,
                subLocationId = subLocationId
            )
                .flowOn(Dispatchers.Main)
                .catch {  cause ->
                    _dialogSkipQueueState.emit(
                        DialogSkipQueueState.FailedDialog(
                            message = cause.message ?: DialogSkipQueueViewModel.ERROR_MESSAGE_UNKNOWN
                        )
                    )

                }
                .collect {
                    when(it) {
                        is SkipQueueState.Success -> {
                            _dialogSkipQueueState.emit(
                                DialogSkipQueueState.SuccessDialog
                            )
                        }
                    }
                }
        }
    }
}