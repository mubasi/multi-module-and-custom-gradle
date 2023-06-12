package id.bluebird.vsm.feature.home.dialog_queue_receipt

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.core.extensions.isUserOfficer
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.user.GetUserByIdState
import id.bluebird.vsm.domain.user.domain.intercator.GetUserId
import id.bluebird.vsm.domain.passenger.GetQueueReceiptState
import id.bluebird.vsm.domain.passenger.TakeQueueState
import id.bluebird.vsm.domain.passenger.domain.cases.GetQueueReceipt
import id.bluebird.vsm.domain.passenger.domain.cases.TakeQueue
import id.bluebird.vsm.domain.user.model.CreateUserResult
import id.bluebird.vsm.feature.home.model.QueueReceiptCache
import id.bluebird.vsm.feature.home.model.TakeQueueCache
import id.bluebird.vsm.feature.home.model.UserInfo
import id.bluebird.vsm.feature.home.queue_ticket.QueueTicketViewModel
import id.bluebird.vsm.feature.select_location.LocationNavigationTemporary
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

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val userInfo get() = mUserInfo

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun setUserInfo(userInfo: UserInfo) {
        mUserInfo = userInfo
    }

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
                            assignLocationId(createResult = it.result)
                            assignSubLocation(createResult = it.result)
                            _dialogQueueReceiptState.emit(DialogQueueReceiptState.GetUserInfoSuccess)
                        }

                        else -> {
                            // do nothing
                        }
                    }
                }
        }
    }

    private fun assignSubLocation(createResult: CreateUserResult) {
        mUserInfo.subLocationId = if (createResult.roleId.isUserOfficer()) {
            createResult.subLocationsId.first()
        } else {
            getLocationNav()?.subLocationId ?: createResult.subLocationsId.first()
        }
    }

    private fun assignLocationId(createResult: CreateUserResult) {
        mUserInfo.locationId = if (createResult.roleId.isUserOfficer()) {
            createResult.locationId
        } else {
            getLocationNav()?.locationId ?: createResult.locationId
        }
    }

    private fun getLocationNav() = LocationNavigationTemporary.getLocationNav()


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
                                queueNumber.value = result.queue.number
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