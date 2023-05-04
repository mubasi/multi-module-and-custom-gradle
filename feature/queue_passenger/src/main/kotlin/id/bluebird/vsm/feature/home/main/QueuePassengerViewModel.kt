package id.bluebird.vsm.feature.home.main

import androidx.annotation.VisibleForTesting
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
import id.bluebird.vsm.domain.user.GetUserAssignmentState
import id.bluebird.vsm.domain.user.domain.intercator.GetUserAssignment
import id.bluebird.vsm.domain.user.model.UserAssignment
import id.bluebird.vsm.feature.home.model.*
import id.bluebird.vsm.feature.select_location.LocationNavigationTemporary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class QueuePassengerViewModel(
    private val getUserByIdForAssignment: GetUserAssignment,
    private val currentQueue: CurrentQueue,
    private val listQueueWaiting: ListQueueWaiting,
    private val listQueueSkipped: ListQueueSkipped,
    private val counterBar: CounterBar,
) : ViewModel() {

    companion object {
        const val ERROR_MESSAGE_UNKNOWN = "Unknown"
        const val EMPTY_STRING = ""
    }

    private val _queuePassengerState: MutableSharedFlow<QueuePassengerState> =
        MutableSharedFlow()
    val queuePassengerState = _queuePassengerState.asSharedFlow()
    private val _locationName: MutableLiveData<String> = MutableLiveData("...")
    private val _subLocationName: MutableLiveData<String> = MutableLiveData("...")
    private val _prefix: MutableLiveData<String> = MutableLiveData("...")
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


    @VisibleForTesting
    fun setLocationName(result: String) {
        _locationName.value = result
    }

    @VisibleForTesting
    fun getLocationName() : String {
        return _locationName.value ?: EMPTY_STRING
    }

    @VisibleForTesting
    fun getSubLocationName() : String {
        return _subLocationName.value ?: EMPTY_STRING
    }

    @VisibleForTesting
    fun getPrefix() : String {
        return _prefix.value ?: EMPTY_STRING
    }

    @VisibleForTesting
    fun setSubLocationName(result: String) {
        _subLocationName.value = result
    }

    @VisibleForTesting
    fun setListQueueWaiting(result: ListQueueResultCache) {
        listQueueWaitingCache = result
    }

    @VisibleForTesting
    fun setListQueueSkipped(result: ListQueueResultCache) {
        listQueueSkippedCache = result
    }

    @VisibleForTesting
    fun setPrefix(result: String?) {
        _prefix.value = result
    }

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
            getUserByIdForAssignment.invoke(UserUtils.getUserId())
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
                        is GetUserAssignmentState.Success -> {
                            val nav = LocationNavigationTemporary.getLocationNav()
                            val dataUser = UserAssignment(
                                id = UserUtils.getUserId(),
                                locationId = it.result.locationId,
                                subLocationId = it.result.subLocationId,
                                locationName = it.result.locationName,
                                subLocationName = it.result.subLocationName,
                                prefix = it.result.prefix,
                                isOfficer = UserUtils.isUserOfficer()
                            )
                            mUserInfo = UserInfo(
                                userId = dataUser.id,
                                locationId = nav?.locationId ?: dataUser.locationId,
                                subLocationId = nav?.subLocationId ?: dataUser.subLocationId
                            )

                            createTitleLocation(userAssignment = dataUser)
                            _queuePassengerState.emit(QueuePassengerState.SuccessGetUser)
                        }
                        GetUserAssignmentState.UserNotFound -> {
                            _queuePassengerState.emit(
                                QueuePassengerState.FailedGetUser(
                                    ERROR_MESSAGE_UNKNOWN
                                )
                            )
                        }
                    }
                }
        }
    }

    fun createTitleLocation(userAssignment: UserAssignment) {
        with(userAssignment) {
            if (isOfficer) {
                _locationName.value = locationName
                _subLocationName.value = subLocationName
                _prefix.value = prefix
            } else {
                val location = LocationNavigationTemporary.getLocationNav()
                _locationName.value = location?.locationName ?: locationName
                _subLocationName.value = location?.subLocationName ?: subLocationName
                _prefix.value = location?.prefix ?: prefix
            }
            titleLocation.value = "${_locationName.value} ${_subLocationName.value}".getLastSync()
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

    fun prosesRitase() {
        viewModelScope.launch {
            _queuePassengerState.emit(QueuePassengerState.ProsesRitase)
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
                QueuePassengerState.ToSearchQueue(
                    locationId = mUserInfo.locationId,
                    subLocationId = mUserInfo.subLocationId,
                    prefix = _prefix.value ?: EMPTY_STRING,
                    listWaiting = listQueueWaitingCache.queue,
                    listSkipped = listQueueSkippedCache.queue
                )
            )
        }
    }


    fun toQrCodeScreen() {
        viewModelScope.launch {
            _queuePassengerState.emit(
                QueuePassengerState.ToQrCodeScreen(
                    locationId = mUserInfo.locationId,
                    subLocationId = mUserInfo.subLocationId,
                    titleLocation = "${_locationName.value} ${_subLocationName.value}"
                )
            )
        }
    }

}