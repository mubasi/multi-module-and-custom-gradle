package id.bluebird.mall.home.queue_ticket

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.navArgument
import id.bluebird.mall.core.utils.hawk.UserUtils
import id.bluebird.mall.domain_pasenger.GetQueueReceiptState
import id.bluebird.mall.domain_pasenger.domain.cases.GetQueueReceipt
import id.bluebird.mall.home.model.QueueReceiptCache
import id.bluebird.mall.home.model.TakeQueueCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
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