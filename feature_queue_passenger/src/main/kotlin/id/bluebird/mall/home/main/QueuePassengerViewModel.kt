package id.bluebird.mall.home.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.mall.core.extensions.StringExtensions.getLastSync
import id.bluebird.mall.domain.user.GetUserByIdState
import id.bluebird.mall.domain.user.domain.intercator.GetUserId
import id.bluebird.mall.domain.user.model.CreateUserResult
import id.bluebird.mall.domain_pasenger.GetCurrentQueueState
import id.bluebird.mall.domain_pasenger.ListQueueSkippedState
import id.bluebird.mall.domain_pasenger.ListQueueWaitingState
import id.bluebird.mall.domain_pasenger.domain.cases.CurrentQueue
import id.bluebird.mall.domain_pasenger.domain.cases.ListQueueSkipped
import id.bluebird.mall.domain_pasenger.domain.cases.ListQueueWaiting
import id.bluebird.mall.home.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class QueuePassengerViewModel(
    private val getUserId : GetUserId,
    private val currentQueue: CurrentQueue,
    private val listQueueWaiting: ListQueueWaiting,
    private val listQueueSkipped: ListQueueSkipped,
) : ViewModel() {

    companion object {
        const val ERROR_MESSAGE_UNKNOWN = "Unknown"
    }

    private val _queuePassengerState: MutableSharedFlow<QueuePassengerState> =
        MutableSharedFlow()
    val queuePassengerState = _queuePassengerState.asSharedFlow()
    val titleLocation: MutableLiveData<String> = MutableLiveData("...")
    var currentQueueCache : CurrentQueueCache = CurrentQueueCache()
    val currentQueueNumber: MutableLiveData<String> = MutableLiveData("...")
    var mUserInfo: UserInfo = UserInfo()
    var listQueueWaitingCache : ListQueueResultCache = ListQueueResultCache(0, queue = ArrayList<QueueReceiptCache>())
    var listQueueSkippedCache : ListQueueResultCache = ListQueueResultCache(0, queue = ArrayList<QueueReceiptCache>())

    fun init() {
        getUserById()
    }

    private fun getUserById() {
        viewModelScope.launch {
            _queuePassengerState.emit(QueuePassengerState.ProsesGetUser)
            getUserId.invoke(null)
                .flowOn(Dispatchers.Main)
                .catch { cause ->
                    _queuePassengerState.emit(
                        QueuePassengerState.FailedGetUser(
                            message = cause.message ?: QueuePassengerViewModel.ERROR_MESSAGE_UNKNOWN
                        )
                    )
                }
                .collect {
                    when (it) {
                        is GetUserByIdState.Success -> {
                            mUserInfo = UserInfo(it.result.id)
                            mUserInfo.locationId = it.result.locationId
                            mUserInfo.subLocationId = it.result.subLocationsId.first()
                            createTitleLocation(it.result)
                            _queuePassengerState.emit(QueuePassengerState.SuccessGetUser)
                        }
                    }
                }
        }
    }

    private fun createTitleLocation(it: CreateUserResult){
        with(it) {
            titleLocation.value = "$locationName $subLocationName".getLastSync()
        }
    }

    fun getCurrentQueue() {
        viewModelScope.launch {
            _queuePassengerState.emit(QueuePassengerState.ProsesCurrentQueue)
            currentQueue.invoke(
                locationId = mUserInfo.locationId
            )
                .flowOn(Dispatchers.Main)
                .catch { cause ->
                    _queuePassengerState.emit(
                        QueuePassengerState.FailedCurrentQueue(
                            message = cause.message ?: QueuePassengerViewModel.ERROR_MESSAGE_UNKNOWN
                        )
                    )
                }
                .collect {
                    when(it) {
                        is GetCurrentQueueState.Success -> {
                            it.currentQueueResult.let { result ->
                                currentQueueCache = CurrentQueueCache(
                                    result.id,
                                    result.number,
                                    result.createdAt
                                )
                                currentQueueNumber.value = result.number
                                _queuePassengerState.emit(
                                    QueuePassengerState.SuccessCurrentQueue
                                )
                            }
                        }
                        else -> {
                            //else
                        }
                    }
                }
        }
    }

    fun getListQueue() {
        viewModelScope.launch {
            _queuePassengerState.emit(QueuePassengerState.ProsesListQueue)
            listQueueWaiting.invoke(
                locationId = mUserInfo.locationId
            )
                .flowOn(Dispatchers.Main)
                .catch { cause ->
                    _queuePassengerState.emit(
                        QueuePassengerState.FailedListQueue(
                            message = cause.message ?: QueuePassengerViewModel.ERROR_MESSAGE_UNKNOWN
                        )
                    )
                }
                .collect {
                    when(it) {
                        is ListQueueWaitingState.Success -> {
                            it.listQueueResult.let { result ->
                                val listQueue = ArrayList<QueueReceiptCache>()
                                result.queue.removeAt(0)
                                result.queue.forEach { item ->
                                    listQueue.add(
                                        QueueReceiptCache(
                                            queueId = item.id,
                                            queueNumber = item.number
                                        )
                                    )
                                }
                                listQueueWaitingCache = ListQueueResultCache(
                                    count = result.count - 1,
                                    queue = listQueue
                                )
                                _queuePassengerState.emit(
                                    QueuePassengerState.SuccessListQueue
                                )
                            }
                        }
                        else -> {
                            //else
                        }
                    }
                }
        }
    }

    fun getListQueueSkipped() {
        viewModelScope.launch {
            _queuePassengerState.emit(QueuePassengerState.ProsesListQueueSkipped)
            listQueueSkipped.invoke(
                locationId = mUserInfo.locationId
            )
                .flowOn(Dispatchers.Main)
                .catch { cause ->
                    _queuePassengerState.emit(
                        QueuePassengerState.FailedListQueueSkipped(
                            message = cause.message ?: QueuePassengerViewModel.ERROR_MESSAGE_UNKNOWN
                        )
                    )
                }
                .collect {
                    when(it) {
                        is ListQueueSkippedState.Success -> {
                            it.listQueueResult.let { result ->
                                val listQueue = ArrayList<QueueReceiptCache>()
                                result.queue.forEach { item ->
                                    listQueue.add(
                                        QueueReceiptCache(
                                            queueId = item.id,
                                            queueNumber = item.number
                                        )
                                    )
                                }
                                listQueueSkippedCache = ListQueueResultCache(
                                    count = result.count,
                                    queue = listQueue
                                )
                                _queuePassengerState.emit(
                                    QueuePassengerState.SuccessListQueueSkipped
                                )
                            }
                        }
                        else -> {
                            //else
                        }
                    }
                }
        }
    }

    fun prosesSkipQueue(){
        viewModelScope.launch {
            _queuePassengerState.emit(QueuePassengerState.ProsesSkipQueue)
        }
    }

    fun prosesQueue() {
        viewModelScope.launch {
            _queuePassengerState.emit(QueuePassengerState.ProsesQueue)
        }
    }

}