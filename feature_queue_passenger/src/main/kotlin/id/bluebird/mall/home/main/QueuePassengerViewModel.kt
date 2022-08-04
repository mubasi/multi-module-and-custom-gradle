package id.bluebird.mall.home.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.mall.home.dialog_queue_receipt.DialogQueueReceiptState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class QueuePassengerViewModel : ViewModel() {

    private val _queuePassengerState: MutableSharedFlow<QueuePassengerState> =
        MutableSharedFlow()
    val queuePassengerState = _queuePassengerState.asSharedFlow()

    fun prosesQueue() {
        viewModelScope.launch {
            _queuePassengerState.emit(QueuePassengerState.ProsesQueue)
        }
    }

}