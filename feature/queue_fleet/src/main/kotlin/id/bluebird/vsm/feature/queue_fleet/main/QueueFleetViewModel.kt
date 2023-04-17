package id.bluebird.vsm.feature.queue_fleet.main

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.core.extensions.StringExtensions.convertCreateAtValue
import id.bluebird.vsm.core.extensions.StringExtensions.getLastSync
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.fleet.DepartFleetState
import id.bluebird.vsm.domain.fleet.GetCountState
import id.bluebird.vsm.domain.fleet.GetListFleetState
import id.bluebird.vsm.domain.fleet.domain.cases.DepartFleet
import id.bluebird.vsm.domain.fleet.domain.cases.GetCount
import id.bluebird.vsm.domain.fleet.domain.cases.GetListFleet
import id.bluebird.vsm.domain.user.GetUserByIdForAssignmentState
import id.bluebird.vsm.domain.user.domain.intercator.GetUserByIdForAssignment
import id.bluebird.vsm.domain.user.model.UserAssignment
import id.bluebird.vsm.feature.queue_fleet.model.CountCache
import id.bluebird.vsm.feature.queue_fleet.model.FleetItem
import id.bluebird.vsm.feature.queue_fleet.model.UserInfo
import id.bluebird.vsm.feature.queue_fleet.request_fleet.RequestFleetDialogViewModel
import id.bluebird.vsm.feature.select_location.LocationNavigationTemporary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class QueueFleetViewModel(
    private val getCount: GetCount,
    private val getUserByIdForAssignment: GetUserByIdForAssignment,
    private val _getFleet: GetListFleet,
    private val departFleet: DepartFleet,
) : ViewModel() {

    companion object {

        const val ERROR_MESSAGE_UNKNOWN = "Unknown"
        const val ZERO = 0
        const val EMPTY_STRING = ""
    }

    val isPerimeter: MutableLiveData<Boolean> = MutableLiveData()
    val counterLiveData: MutableLiveData<CountCache> = MutableLiveData()
    val titleLocation: MutableLiveData<String> = MutableLiveData("...")
    private val _queueFleetState: MutableSharedFlow<QueueFleetState> =
        MutableSharedFlow()
    val queueFleetState: SharedFlow<QueueFleetState> = _queueFleetState.asSharedFlow()
    private var mCountCache: CountCache = CountCache()
    var mUserInfo: UserInfo = UserInfo()
    private val _fleetItems: MutableList<FleetItem> = mutableListOf()
    private val _locationName: MutableLiveData<String> = MutableLiveData("...")
    private val _subLocationName: MutableLiveData<String> = MutableLiveData("...")

    @VisibleForTesting
    fun setUserInfo(userInfo: UserInfo) {
        mUserInfo = userInfo
    }

    @VisibleForTesting
    fun valUserInfo(): UserInfo {
        return mUserInfo
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

    @VisibleForTesting
    fun runTestGetUserById() {
        getUserById()
    }

    @VisibleForTesting
    fun setLocationName(result: String) {
        _locationName.value = result
    }

    @VisibleForTesting
    fun setSubLocationName(result: String) {
        _subLocationName.value = result
    }

    @VisibleForTesting
    fun getFleetItem(): List<FleetItem> {
        return _fleetItems
    }

    @VisibleForTesting
    fun getCountCache(): CountCache {
        return mCountCache
    }

    fun init() {
        if (LocationNavigationTemporary.isLocationNavAvailable()
                .not() && UserUtils.isUserOfficer().not()
        ) {
            viewModelScope.launch {
                _queueFleetState.emit(QueueFleetState.ProgressHolder)
                delay(400)
                _queueFleetState.emit(QueueFleetState.ToSelectLocation)
            }
        } else {
            getUserById()
        }
    }

    fun initLocation(locationId: Long, subLocationId: Long) {
        if (locationId > 0 && subLocationId > 0) {
            mUserInfo.subLocationId = subLocationId
            mUserInfo.locationId = locationId
        }
    }

    private fun getUserById() {
        viewModelScope.launch {
            _queueFleetState.emit(QueueFleetState.ProgressGetUser)
                val nav = LocationNavigationTemporary.getLocationNav()
                getUserByIdForAssignment.invoke(
                    UserUtils.getUserId(),
                    locationIdNav = nav?.locationId,
                    subLocationIdNav = nav?.subLocationId
                )
                    .catch { cause ->
                        _queueFleetState.emit(
                            QueueFleetState.FailedGetUser(
                                message = cause.message ?: ERROR_MESSAGE_UNKNOWN
                            )
                        )
                    }
                    .collect {
                        when (it) {
                            is GetUserByIdForAssignmentState.Success -> {
                                mUserInfo = UserInfo(
                                    userId = it.result.id,
                                    locationId = it.result.locationId,
                                    subLocationId = it.result.subLocationId
                                )
                                createTitleLocation(userAssignment = it.result)
                                _queueFleetState.emit(QueueFleetState.GetUserInfoSuccess)
                            }
                            GetUserByIdForAssignmentState.UserNotFound -> {
                                _queueFleetState.emit(
                                    QueueFleetState.FailedGetUser(
                                        ERROR_MESSAGE_UNKNOWN
                                    )
                                )
                            }
                        }
                    }
        }
    }

    private fun createTitleLocation(userAssignment: UserAssignment) {
        with(userAssignment) {
            if (isOfficer) {
                _locationName.value = locationName
                _subLocationName.value = subLocationName
            } else {
                val location = LocationNavigationTemporary.getLocationNav()
                _locationName.value = location?.locationName ?: EMPTY_STRING
                _subLocationName.value = location?.subLocationName ?: EMPTY_STRING
            }
            titleLocation.value = "${_locationName.value} ${_subLocationName.value}".getLastSync()
        }
    }

    fun getCounter() {
        viewModelScope.launch {
            if (!mCountCache.isInit) {
                getCount.invoke(
                    subLocationId = mUserInfo.subLocationId,
                    locationId = mUserInfo.locationId
                )
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
        /**
         * disable this condition (showRecordRitase)
         * for not selected queue and set fleet only ada penumpang or tanpa penumpang
         * this disable in version 1.1.2
         */
//        if (isWithPassenger && queueId.isBlank()) {
//            showRecordRitase(fleetItem, queueId)
//            return
//        }
        viewModelScope.launch {
            _queueFleetState.emit(QueueFleetState.ProgressDepartFleet)
            departFleet.invoke(
                locationId = mUserInfo.locationId,
                subLocationId = mUserInfo.subLocationId,
                fleetNumber = fleetItem.name,
                isWithPassenger = isWithPassenger,
                departFleetItems = listOf(fleetItem.id),
                queueNumber = queueId
            )
                .flowOn(Dispatchers.Main)
                .catch { error ->
                    _queueFleetState.emit(
                        QueueFleetState.FailedDepart(
                            error.message ?: ERROR_MESSAGE_UNKNOWN
                        )
                    )
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
            _queueFleetState.emit(QueueFleetState.NotifyDataFleetChanged(_fleetItems.toList()))
        }
    }

    fun showRecordRitase(fleetItem: FleetItem, queueId: String) {
        viewModelScope.launch {
            _queueFleetState.emit(
                QueueFleetState.RecordRitaseToDepart(
                    fleetItem,
                    mUserInfo.locationId,
                    mUserInfo.subLocationId,
                    queueId
                )
            )
        }
    }

    fun requestDepart(fleetItem: FleetItem) {
        viewModelScope.launch {
            delay(50)
            _queueFleetState.emit(
                QueueFleetState.RequestDepartFleet(
                    fleetItem,
                    mUserInfo.locationId,
                    mUserInfo.subLocationId
                )
            )
        }
    }

    fun onErrorFromDialog(throwable: Throwable) {
        viewModelScope.launch {
            _queueFleetState.emit(QueueFleetState.FailedGetQueue(throwable))
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
            delay(200)
            _queueFleetState.emit(QueueFleetState.AddFleet(subLocationId = mUserInfo.subLocationId))
            delay(200)
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
            mCountCache.stock++
            calculateRequestAfterAddStock()
            counterLiveData.value = mCountCache
            viewModelScope.launch {
                _fleetItems.add(fleetItem)
                _queueFleetState.emit(QueueFleetState.NotifyDataFleetChanged(_fleetItems.toList()))
            }
        }
    }

    private fun calculateRequestAfterAddStock() {
        if (mCountCache.request > ZERO) {
            mCountCache.request--
        }
    }

    fun showSearchQueue(
        fleetItem: FleetItem,
        currentQueueId: String,
        locationId: Long,
        subLocationId: Long
    ) {
        viewModelScope.launch {
            _queueFleetState.emit(
                QueueFleetState.SearchQueueToDepart(
                    fleetItem,
                    locationId,
                    subLocationId,
                    currentQueueId
                )
            )
        }
    }

    fun getFleetList() {
        viewModelScope.launch {
            _queueFleetState.emit(QueueFleetState.ProgressGetFleetList)
            if (_fleetItems.isNotEmpty()) {
                _queueFleetState.emit(QueueFleetState.GetListSuccess(_fleetItems.toList()))
            } else {
                _getFleet.invoke(mUserInfo.subLocationId)
                    .flowOn(Dispatchers.Main)
                    .catch { cause: Throwable ->
                        _queueFleetState.emit(QueueFleetState.FailedGetList(cause))
                        _queueFleetState.emit(QueueFleetState.GetListEmpty)
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
                                _queueFleetState.emit(QueueFleetState.GetListSuccess(_fleetItems.toList()))
                            }
                        }
                    }
            }
        }
    }

    fun refresh() {
        mCountCache = CountCache()
        init()
    }

    fun goToQrCodeScreen() {
        viewModelScope.launch {
            _queueFleetState.emit(
                QueueFleetState.GoToQrCodeScreen(
                    locationId = mUserInfo.locationId,
                    subLocationId = mUserInfo.subLocationId,
                    titleLocation = "${_locationName.value} ${_subLocationName.value}"
                )
            )
        }
    }
}