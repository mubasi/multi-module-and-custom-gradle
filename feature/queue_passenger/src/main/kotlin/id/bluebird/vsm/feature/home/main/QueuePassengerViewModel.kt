package id.bluebird.vsm.feature.home.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.core.extensions.StringExtensions.getLastSync
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.passenger.CounterBarState
import id.bluebird.vsm.domain.passenger.GetCurrentQueueState
import id.bluebird.vsm.domain.passenger.ListQueueSkippedState
import id.bluebird.vsm.domain.passenger.ListQueueWaitingState
import id.bluebird.vsm.domain.passenger.domain.cases.CounterBar
import id.bluebird.vsm.domain.passenger.domain.cases.CurrentQueue
import id.bluebird.vsm.domain.passenger.domain.cases.ListQueueSkipped
import id.bluebird.vsm.domain.passenger.domain.cases.ListQueueWaiting
import id.bluebird.vsm.domain.user.GetUserByIdForAssignmentState
import id.bluebird.vsm.domain.user.domain.intercator.GetUserByIdForAssignment
import id.bluebird.vsm.domain.user.model.UserAssignment
import id.bluebird.vsm.feature.select_location.LocationNavigationTemporary
import id.bluebird.vsm.feature.home.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class QueuePassengerViewModel(
    private val getUserByIdForAssignment: GetUserByIdForAssignment,
    private val currentQueue: CurrentQueue,
    private val listQueueWaiting: ListQueueWaiting,
    private val listQueueSkipped: ListQueueSkipped,
    private val counterBar: CounterBar,
) : ViewModel() {

    companion object {
        const val ERROR_MESSAGE_UNKNOWN = "Unknown"
    }

    private val _queuePassengerState: MutableSharedFlow<QueuePassengerState> =
        MutableSharedFlow()
    val queuePassengerState = _queuePassengerState.asSharedFlow()
    val titleLocation: MutableLiveData<String> = MutableLiveData("...")
    var currentQueueCache: CurrentQueueCache = CurrentQueueCache()
    var currentCounterBar: MutableLiveData<CounterBarCache> = MutableLiveData()
    val currentQueueNumber: MutableLiveData<String> = MutableLiveData("...")
    var mUserInfo: UserInfo = UserInfo()
    var listQueueWaitingCache: ListQueueResultCache = ListQueueResultCache(0, queue = ArrayList())
    var listQueueSkippedCache: ListQueueResultCache = ListQueueResultCache(0, queue = ArrayList())
    private var _waitingQueueCount: MutableLiveData<CharSequence> = MutableLiveData("0")
    val waitingQueueCount: LiveData<CharSequence> = _waitingQueueCount
    private var _skippedQueueCount: MutableLiveData<CharSequence> = MutableLiveData("0")
    val skippedQueueCount: LiveData<CharSequence> = _skippedQueueCount

    fun init() {
        if (LocationNavigationTemporary.isLocationNavAvailable()
                .not() && UserUtils.isUserOfficer().not()
        ) {
            viewModelScope.launch {
                _queuePassengerState.emit(QueuePassengerState.ProgressHolder)
                delay(400)
                _queuePassengerState.emit(QueuePassengerState.ToSelectLocation)
            }
        } else {
            getUserById()
        }
    }

    private fun getUserById() {
        viewModelScope.launch {
            _queuePassengerState.emit(QueuePassengerState.ProsesGetUser)
            val nav = LocationNavigationTemporary.getLocationNav()
            getUserByIdForAssignment.invoke(UserUtils.getUserId(), locationIdNav = nav?.locationId, subLocationIdNav = nav?.subLocationId)
                .flowOn(Dispatchers.Main)
                .catch { cause ->
                    _queuePassengerState.emit(
                        QueuePassengerState.FailedGetUser(
                            message = cause.message ?: ERROR_MESSAGE_UNKNOWN
                        )
                    )
                }
                .collect {
                    when (it) {
                        is GetUserByIdForAssignmentState.Success -> {
                            mUserInfo = UserInfo(userId = it.result.id, locationId = it.result.locationId, subLocationId = it.result.subLocationId)
                            createTitleLocation(userAssignment = it.result)
                            _queuePassengerState.emit(QueuePassengerState.SuccessGetUser)
                        }
                        GetUserByIdForAssignmentState.UserNotFound -> {
                            _queuePassengerState.emit(QueuePassengerState.FailedGetUser(
                                ERROR_MESSAGE_UNKNOWN))
                        }
                    }
                }
        }
    }

    private fun createTitleLocation(userAssignment: UserAssignment) {
        with(userAssignment) {
            titleLocation.value = if (isOfficer) {
                "$locationName $subLocationName".getLastSync()
            } else {
                val location = LocationNavigationTemporary.getLocationNav()
                location?.let {
                    "${it.locationName} ${it.subLocationName}".getLastSync()
                }
            }
        }
    }

    fun getCurrentQueue() {
        viewModelScope.launch {
            _queuePassengerState.emit(QueuePassengerState.ProsesCurrentQueue)
            currentQueue.invoke(
                locationId = mUserInfo.locationId,
                subLocationId = mUserInfo.subLocationId
            )
                .flowOn(Dispatchers.Main)
                .catch { cause ->
                    _queuePassengerState.emit(
                        QueuePassengerState.FailedCurrentQueue(
                            message = cause.message ?: ERROR_MESSAGE_UNKNOWN
                        )
                    )
                }
                .collect {
                    when (it) {
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
                locationId = mUserInfo.locationId,
                subLocationId = mUserInfo.subLocationId
            )
                .flowOn(Dispatchers.Main)
                .catch { cause ->
                    _queuePassengerState.emit(
                        QueuePassengerState.FailedListQueue(
                            message = cause.message ?: ERROR_MESSAGE_UNKNOWN
                        )
                    )
                }
                .collect {
                    when (it) {
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
                                _waitingQueueCount.value = listQueueWaitingCache.count.toString()
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
                locationId = mUserInfo.locationId,
                subLocationId = mUserInfo.subLocationId
            )
                .flowOn(Dispatchers.Main)
                .catch { cause ->
                    _queuePassengerState.emit(
                        QueuePassengerState.FailedListQueueSkipped(
                            message = cause.message ?: ERROR_MESSAGE_UNKNOWN
                        )
                    )
                }
                .collect {
                    when (it) {
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
                                _skippedQueueCount.value = listQueueSkippedCache.count.toString()
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

    fun prosesSkipQueue() {
        viewModelScope.launch {
            _queuePassengerState.emit(QueuePassengerState.ProsesSkipQueue)
        }
    }

    fun prosesQueue() {
        viewModelScope.launch {
            _queuePassengerState.emit(QueuePassengerState.ProsesQueue)
        }
    }

    fun prosesDeleteQueue(queueReceiptCache: QueueReceiptCache) {
        viewModelScope.launch {
            _queuePassengerState.emit(QueuePassengerState.ProsesDeleteQueueSkipped(queueReceiptCache))
        }
    }

    fun prosesRestoreQueue(queueReceiptCache: QueueReceiptCache) {
        viewModelScope.launch {
            _queuePassengerState.emit(
                QueuePassengerState.ProsesRestoreQueueSkipped(
                    queueReceiptCache
                )
            )
        }
    }


    fun getCounterBar() {
        viewModelScope.launch {
            _queuePassengerState.emit(QueuePassengerState.ProsesCounterBar)
            counterBar.invoke(
                locationId = mUserInfo.locationId,
                subLocationId = mUserInfo.subLocationId
            )
                .flowOn(Dispatchers.Main)
                .catch { cause ->
                    _queuePassengerState.emit(
                        QueuePassengerState.FailedCounterBar(
                            message = cause.message ?: ERROR_MESSAGE_UNKNOWN
                        )
                    )
                }
                .collect {
                    when (it) {
                        is CounterBarState.Success -> {
                            it.counterBarResult.let { result ->
                                currentCounterBar.value = CounterBarCache(
                                    result.locationId,
                                    result.ongoing,
                                    result.skipped,
                                    result.ritese,
                                    result.modifiedAt
                                )
                                _queuePassengerState.emit(
                                    QueuePassengerState.SuccessCounterBar
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

    fun searchQueue() {
        viewModelScope.launch {
            _queuePassengerState.emit(
                QueuePassengerState.SearchQueue(
                    locationId = mUserInfo.locationId,
                    subLocationId = mUserInfo.subLocationId
                )
            )
        }
    }

}