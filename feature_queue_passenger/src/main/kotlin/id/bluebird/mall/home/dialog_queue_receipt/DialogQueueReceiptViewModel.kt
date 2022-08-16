package id.bluebird.mall.home.dialog_queue_receipt

import android.text.Html
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.mall.core.utils.hawk.UserUtils
import id.bluebird.mall.domain.user.GetUserByIdState
import id.bluebird.mall.domain.user.domain.intercator.GetUserId
import id.bluebird.mall.domain_pasenger.GetQueueReceiptState
import id.bluebird.mall.domain_pasenger.TakeQueueState
import id.bluebird.mall.domain_pasenger.domain.cases.GetQueueReceipt
import id.bluebird.mall.domain_pasenger.domain.cases.TakeQueue
import id.bluebird.mall.home.model.QueueReceiptCache
import id.bluebird.mall.home.model.TakeQueueCache
import id.bluebird.mall.home.model.UserInfo
import id.bluebird.mall.home.queue_ticket.QueueTicketViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class DialogQueueReceiptViewModel(
    private val getQueueReceipt: GetQueueReceipt,
    private val takeQueueReceipt: TakeQueue,
    private val getUserId : GetUserId
) : ViewModel() {

    companion object {
        const val ERROR_MESSAGE_UNKNOWN = "Unknown"
    }

    private var mQueueReceiptCache : QueueReceiptCache = QueueReceiptCache()
    var takeQueueCache : TakeQueueCache = TakeQueueCache()
    private val _dialogQueueReceiptState: MutableSharedFlow<DialogQueueReceiptState> =
        MutableSharedFlow()
    val dialogQueueReceiptState = _dialogQueueReceiptState.asSharedFlow()
    val queueNumber: MutableLiveData<String> = MutableLiveData("")

    private lateinit var mUserInfo: UserInfo

    fun init() {
        getUserById()
    }

    private fun getUserById() {
        viewModelScope.launch {
            _dialogQueueReceiptState.emit(DialogQueueReceiptState.ProgressGetUser)
            getUserId.invoke(UserUtils.getUserId())
                .flowOn(Dispatchers.Main)
                .catch { cause ->
                    _dialogQueueReceiptState.emit(
                        DialogQueueReceiptState.FailedGetUser(
                            message = cause.message ?: ERROR_MESSAGE_UNKNOWN
                        )
                    )
                }
                .collect {
                    when (it) {
                        is GetUserByIdState.Success -> {
                            mUserInfo = UserInfo(it.result.id)
                            mUserInfo.locationId = it.result.locationId
                            mUserInfo.subLocationId = it.result.subLocationsId.first()
                            _dialogQueueReceiptState.emit(DialogQueueReceiptState.GetUserInfoSuccess)
                        }
                    }
                }
        }
    }

    fun getQueue() {
        viewModelScope.launch {
            _dialogQueueReceiptState.emit(
                DialogQueueReceiptState.ProgressGetQueue
            )

            getQueueReceipt.invoke(
                0,
                1,
                mUserInfo.locationId,
                "",
                mUserInfo.subLocationId,
                ""
            )
                .flowOn(Dispatchers.Main)
                .catch { cause ->
                    _dialogQueueReceiptState.emit(
                        DialogQueueReceiptState.FailedGetQueue(
                            message = cause.message ?: QueueTicketViewModel.ERROR_MESSAGE_UNKNOWN
                        )
                    )
                }
                .collect {
                    when (it) {
                        is GetQueueReceiptState.Success -> {
                            it.queueResult.let { result ->
                                mQueueReceiptCache = QueueReceiptCache(
                                    result.queue.id,
                                    result.queue.number,
                                )
                                val number = Html.fromHtml("No. antrian <font color=#005eb8>${result.queue.number}</font>",1)
                                queueNumber.value = "$number"
                                _dialogQueueReceiptState.emit(
                                    DialogQueueReceiptState.GetQueueSuccess
                                )
                            }
                        }
                    }

                }
        }
    }

    fun requestQueue() {
        viewModelScope.launch {
            _dialogQueueReceiptState.emit(
                DialogQueueReceiptState.ProgressGetQueue
            )
            takeQueueReceipt.invoke(
                0,
                2,
                mUserInfo.locationId,
                mQueueReceiptCache.queueNumber,
                mUserInfo.subLocationId,
                ""
            )
                .flowOn(Dispatchers.Main)
                .catch { cause ->
                    Log.e("requestQueue", cause.message.toString())
                    _dialogQueueReceiptState.emit(
                        DialogQueueReceiptState.FailedTakeQueue(
                            message = cause.message ?: QueueTicketViewModel.ERROR_MESSAGE_UNKNOWN
                        )
                    )
                }
                .collect {
                    when (it) {
                        is TakeQueueState.Success -> {
                            it.takeQueue.let { result ->
                                takeQueueCache = TakeQueueCache(
                                    result.queue.id,
                                    result.queue.number,
                                    result.queue.createdAt,
                                    result.queue.message,
                                    result.queue.currentQueue,
                                    result.queue.totalQueue,
                                    result.queue.subLocationId
                                )
                                _dialogQueueReceiptState.emit(
                                    DialogQueueReceiptState.TakeQueueSuccess
                                )
                            }
                        }
                    }

                }
        }
    }

    fun cancelDialog() {
        viewModelScope.launch {
            _dialogQueueReceiptState.emit(DialogQueueReceiptState.CancelDialog)
        }
    }
}