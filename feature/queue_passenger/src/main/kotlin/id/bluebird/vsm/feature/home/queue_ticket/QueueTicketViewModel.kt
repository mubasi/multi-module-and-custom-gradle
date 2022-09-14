package id.bluebird.vsm.feature.home.queue_ticket

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.domain.passenger.domain.cases.GetQueueReceipt
import id.bluebird.vsm.feature.home.model.TakeQueueCache
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class QueueTicketViewModel(
    private val getQueueReceipt: GetQueueReceipt,
    ) : ViewModel() {

    companion object {
        const val ERROR_MESSAGE_UNKNOWN = "Unknown"
    }

    val takeQueueCache: MutableLiveData<TakeQueueCache> = MutableLiveData()
    private val _queueTicketState: MutableSharedFlow<QueueTicketState> =
        MutableSharedFlow()
    val queueTicketState = _queueTicketState.asSharedFlow()


    fun prosesDialog() {
        viewModelScope.launch {
            _queueTicketState.emit(QueueTicketState.ProsesTicket)
        }
    }
}