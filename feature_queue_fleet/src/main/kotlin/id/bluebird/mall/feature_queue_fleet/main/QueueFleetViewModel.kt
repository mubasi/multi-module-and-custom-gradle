package id.bluebird.mall.feature_queue_fleet.main

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.mall.core.extensions.StringExtensions.convertCreateAtValue
import id.bluebird.mall.core.extensions.StringExtensions.getLastSync
import id.bluebird.mall.core.utils.hawk.UserUtils
import id.bluebird.mall.domain.user.GetUserByIdState
import id.bluebird.mall.domain.user.domain.intercator.GetUserId
import id.bluebird.mall.domain.user.model.CreateUserResult
import id.bluebird.mall.domain_fleet.DepartFleetState
import id.bluebird.mall.domain_fleet.GetCountState
import id.bluebird.mall.domain_fleet.GetListFleetState
import id.bluebird.mall.domain_fleet.domain.cases.DepartFleet
import id.bluebird.mall.domain_fleet.domain.cases.GetCount
import id.bluebird.mall.domain_fleet.domain.cases.GetListFleet
import id.bluebird.mall.domain_pasenger.GetCurrentQueueState
import id.bluebird.mall.domain_pasenger.domain.cases.GetCurrentQueue
import id.bluebird.mall.feature_queue_fleet.model.CountCache
import id.bluebird.mall.feature_queue_fleet.model.FleetItem
import id.bluebird.mall.feature_queue_fleet.model.UserInfo
import id.bluebird.mall.feature_queue_fleet.request_fleet.RequestFleetDialogViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class QueueFleetViewModel(
    private val getCount: GetCount,
    private val getUserId: GetUserId,
    private val _getFleet: GetListFleet,
    private val departFleet: DepartFleet,
    private val getCurrentQueue: GetCurrentQueue,
) : ViewModel() {

    companion object {
        const val ERROR_MESSAGE_UNKNOWN = "Unknown"
    }

    val isPerimeter: MutableLiveData<Boolean> = MutableLiveData()
    val counterLiveData: MutableLiveData<CountCache> = MutableLiveData()
    val titleLocation: MutableLiveData<String> = MutableLiveData("...")
    private val _queueFleetState: MutableSharedFlow<QueueFleetState> =
        MutableSharedFlow()
    val queueFleetState: SharedFlow<QueueFleetState> = _queueFleetState.asSharedFlow()

    private var mCountCache: CountCache = CountCache()
    private lateinit var mUserInfo: UserInfo
    private val _fleetItems: MutableList<FleetItem> = mutableListOf()

    @VisibleForTesting
    fun setUserInfo(userInfo: UserInfo) {
        mUserInfo = userInfo
    }

    @VisibleForTesting
    fun setCountCache(countCache: CountCache) {
        mCountCache = countCache
        counterLiveData.value = mCountCache
    }

    @VisibleForTesting
    fun setFleetItems(list: List<FleetItem>) {
        _fleetItems.addAll(list)
    }

    fun init() {
        getUserById()
    }

    private fun getUserById() {
        viewModelScope.launch {
            _queueFleetState.emit(QueueFleetState.ProgressGetUser)
            getUserId.invoke(UserUtils.getUserId())
                .catch { cause ->
                    _queueFleetState.emit(
                        QueueFleetState.FailedGetUser(
                            message = cause.message ?: ERROR_MESSAGE_UNKNOWN
                        )
                    )
                }
                .flowOn(Dispatchers.Main)
                .collect {
                    when (it) {
                        is GetUserByIdState.Success -> {
                            mUserInfo = UserInfo(it.result.id)
                            mUserInfo.locationId = it.result.locationId
                            mUserInfo.subLocationId = it.result.subLocationsId.first()
                            createTitleLocation(it.result)
                            _queueFleetState.emit(QueueFleetState.GetUserInfoSuccess)
                        }
                    }
                }
        }
    }

    private fun createTitleLocation(it:CreateUserResult){
        with(it) {
            titleLocation.value = "$locationName $subLocationName".getLastSync()
        }
    }

    fun getCounter() {
        viewModelScope.launch {
            if (!mCountCache.isInit) {
                getCount.invoke(mUserInfo.subLocationId)
                    .flowOn(Dispatchers.Main)
                    .catch { cause ->
                        _queueFleetState.emit(
                            QueueFleetState.FailedGetCounter(
                                message = cause.message ?: ERROR_MESSAGE_UNKNOWN
                            )
                        )
                    }
                    .collect {
                        when (it) {
                            is GetCountState.Success -> {
                                it.countResult.let { result ->
                                    mCountCache = CountCache(
                                        isInit = true,
                                        stock = result.stock,
                                        request = result.request,
                                        ritase = result.ritase
                                    )
                                    counterLiveData.postValue(mCountCache)
                                }
                            }
                        }

                    }
            }
        }
    }


    fun updateRequestCount(count: Long) {
        if (count >= RequestFleetDialogViewModel.MINIMUM_COUNTER_VALUE) {
            mCountCache.request = count
            counterLiveData.value = mCountCache
        }
    }

    fun departFleet(fleetItem: FleetItem, isWithPassenger: Boolean = false, queueId: String = "") {
        if (isWithPassenger && queueId.isBlank()) {
            showRecordRitase(fleetItem, queueId)
            return
        }
        viewModelScope.launch {
            _queueFleetState.emit(QueueFleetState.ProgressDepartFleet)
            departFleet.invoke(
                mUserInfo.subLocationId,
                fleetItem.name,
                isWithPassenger,
                listOf(fleetItem.id),
                queueId
            )
                .flowOn(Dispatchers.Main)
                .catch { error ->
                    _queueFleetState.emit(QueueFleetState.FailedDepart(error.message ?: ERROR_MESSAGE_UNKNOWN))
                }
                .collectLatest {
                    when (it) {
                        is DepartFleetState.Success -> {
                            _queueFleetState.emit(
                                QueueFleetState.SuccessDepartFleet(
                                    it.fleetDepartResult.taxiNo,
                                    isWithPassenger
                                )
                            )

                            delay(100)
                            mCountCache.stock -= 1
                            if (mCountCache.stock < 0)
                                mCountCache.stock = 0
                            if (isWithPassenger)
                                mCountCache.ritase += 1

                            counterLiveData.postValue(mCountCache)
                        }
                    }
                }
        }
    }

    fun removeFleet(fleetNumber: String) {
        val fleetIndex = _fleetItems.indexOfFirst { it.name == fleetNumber }
        if (fleetIndex < 0)
            return

        _fleetItems.removeAt(fleetIndex)
        viewModelScope.launch {
            _queueFleetState.emit(QueueFleetState.FleetDeparted(_fleetItems, fleetIndex))
        }
    }

    fun showRecordRitase(fleetItem: FleetItem, queueId: String?) {
        viewModelScope.launch {
            var queue = queueId ?: ""
            with(queueId){
                if (isNullOrBlank()) {
                    getCurrentQueue
                        .invoke()
                        .flowOn(Dispatchers.Main)
                        .catch {
                            queue = ""
                        }.collect {
                            when (it) {
                                is GetCurrentQueueState.Success -> queue = it.currentQueueResult.number
                            }
                        }
                } else {
                    this
                }
            }
            _queueFleetState.emit(QueueFleetState.RecordRitaseToDepart(fleetItem, mUserInfo.subLocationId, queue))
        }
    }

    fun requestDepart(fleetItem: FleetItem) {
        viewModelScope.launch {
            _queueFleetState.emit(QueueFleetState.RequestDepartFleet(fleetItem))
        }
    }

    fun searchFleet() {
        viewModelScope.launch {
            _queueFleetState.emit(
                QueueFleetState.SearchFleet(
                    subLocationId = mUserInfo.subLocationId,
                    list = _fleetItems
                )
            )
        }
    }

    fun showRequestFleet() {
        viewModelScope.launch {
            _queueFleetState.emit(QueueFleetState.ShowRequestFleet(subLocationId = mUserInfo.subLocationId))
        }
    }

    fun addFleet() {
        viewModelScope.launch {
            _queueFleetState.emit(QueueFleetState.AddFleet(subLocationId = mUserInfo.subLocationId))
        }
    }

    fun stateIdle() {
        viewModelScope.launch {
            _queueFleetState.emit(QueueFleetState.Idle)
        }
    }

    fun addSuccess(fleetItem: FleetItem?) {
        if (fleetItem != null) {
            mCountCache.stock += 1
            counterLiveData.value = mCountCache
            viewModelScope.launch {
                _fleetItems.add(fleetItem)
                _queueFleetState.emit(QueueFleetState.AddFleetSuccess(_fleetItems))
            }
        }
    }

    fun showSearchQueue(fleetItem: FleetItem, currentQueueId: String) {
        viewModelScope.launch {
            _queueFleetState.emit(QueueFleetState.SearchQueueToDepart(fleetItem, mUserInfo.locationId, mUserInfo.subLocationId, currentQueueId))
        }
    }

    fun departSuccess(list: List<FleetItem>) {
        viewModelScope.launch {
            _fleetItems.clear()
            _fleetItems.addAll(list)
            _queueFleetState.emit(QueueFleetState.GetListSuccess(_fleetItems))
        }
    }

    fun getFleetList() {
        viewModelScope.launch {
            _queueFleetState.emit(QueueFleetState.ProgressGetFleetList)
            if (_fleetItems.isNotEmpty()) {
                _queueFleetState.emit(QueueFleetState.GetListSuccess(_fleetItems))
            } else {
                _getFleet.invoke(mUserInfo.subLocationId)
                    .flowOn(Dispatchers.Main)
                    .catch { cause: Throwable ->
                        _queueFleetState.emit(QueueFleetState.FailedGetList(cause))
                    }
                    .collect {
                        when (it) {
                            GetListFleetState.EmptyResult -> {
                                _queueFleetState.emit(QueueFleetState.GetListEmpty)
                            }
                            is GetListFleetState.Success -> {
                                it.list.forEach { item ->
                                    _fleetItems.add(
                                        FleetItem(
                                            id = item.fleetId,
                                            name = item.fleetName,
                                            arriveAt = item.arriveAt.convertCreateAtValue()
                                        )
                                    )
                                }
                                _queueFleetState.emit(QueueFleetState.GetListSuccess(_fleetItems))
                            }
                        }
                    }
            }
        }
    }
}