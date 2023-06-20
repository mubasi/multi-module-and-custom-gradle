package id.bluebird.vsm.feature.queue_car_fleet.main

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
import id.bluebird.vsm.feature.queue_car_fleet.depart_fleet.FragmentDepartCarFleetDialog
import id.bluebird.vsm.feature.queue_car_fleet.model.CountCacheCarFleet
import id.bluebird.vsm.feature.queue_car_fleet.model.CarFleetItem
import id.bluebird.vsm.feature.queue_car_fleet.model.UserRoleInfo
import id.bluebird.vsm.feature.queue_car_fleet.request_fleet.RequestCarFleetDialogViewModel
import id.bluebird.vsm.feature.select_location.LocationNavigationTemporary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class QueueCarFleetViewModel(
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

    val isDeposition: MutableLiveData<Boolean> = MutableLiveData()
    val counterLiveData: MutableLiveData<CountCacheCarFleet> = MutableLiveData()
    val titleLocation: MutableLiveData<String> = MutableLiveData("...")
    private val _queueCarFleetState: MutableSharedFlow<QueueCarFleetState> =
        MutableSharedFlow()
    val queueCarFleetState: SharedFlow<QueueCarFleetState> = _queueCarFleetState.asSharedFlow()
    private var mCountCacheCarFleet: CountCacheCarFleet = CountCacheCarFleet()
    var mUserRoleInfo: UserRoleInfo = UserRoleInfo()
    private val _fleetItems: MutableList<CarFleetItem> = mutableListOf()
    private val _locationName: MutableLiveData<String> = MutableLiveData("...")
    private val _subLocationName: MutableLiveData<String> = MutableLiveData("...")
    private val idDeposition: MutableLiveData<Long> = MutableLiveData()

    @VisibleForTesting
    fun setUserInfo(userRoleInfo: UserRoleInfo) {
        mUserRoleInfo = userRoleInfo
    }

    @VisibleForTesting
    fun valUserInfo(): UserRoleInfo {
        return mUserRoleInfo
    }

    @VisibleForTesting
    fun setCountCache(countCacheCarFleet: CountCacheCarFleet?) {
        if (countCacheCarFleet != null) {
            mCountCacheCarFleet = countCacheCarFleet
        }
        counterLiveData.value = mCountCacheCarFleet
    }

    @VisibleForTesting
    fun setFleetItems(list: List<CarFleetItem>) {
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
    fun getFleetItem(): List<CarFleetItem> {
        return _fleetItems
    }

    @VisibleForTesting
    fun getCountCache(): CountCacheCarFleet {
        return mCountCacheCarFleet
    }

    @VisibleForTesting
    fun setTitleLocation(result: String?) {
        titleLocation.value = result ?: EMPTY_STRING
    }

    @VisibleForTesting
    fun setIdDeposition(result : Long) {
        idDeposition.value = result
    }

    fun init() {
        if (LocationNavigationTemporary.isLocationNavAvailable()
                .not() && UserUtils.isUserOfficer().not()
        ) {
            viewModelScope.launch {
                _queueCarFleetState.emit(QueueCarFleetState.ProgressHolder)
                delay(400)
                _queueCarFleetState.emit(QueueCarFleetState.ToSelectLocation)
            }
        } else {
            getUserById()
        }
    }

    fun initLocation(locationId: Long, subLocationId: Long) {
        if (locationId > 0 && subLocationId > 0) {
            mUserRoleInfo.subLocationId = subLocationId
            mUserRoleInfo.locationId = locationId
        }
    }

    private fun getUserById() {
        viewModelScope.launch {
            _queueCarFleetState.emit(QueueCarFleetState.ProgressGetUser)
                val nav = LocationNavigationTemporary.getLocationNav()
                getUserByIdForAssignment.invoke(
                    UserUtils.getUserId(),
                    locationIdNav = nav?.locationId,
                    subLocationIdNav = nav?.subLocationId
                )
                    .catch { cause ->
                        _queueCarFleetState.emit(
                            QueueCarFleetState.FailedGetUser(
                                message = cause.message ?: ERROR_MESSAGE_UNKNOWN
                            )
                        )
                    }
                    .collect {
                        when (it) {
                            is GetUserByIdForAssignmentState.Success -> {
                                mUserRoleInfo = UserRoleInfo(
                                    userId = it.result.id,
                                    locationId = it.result.locationId,
                                    subLocationId = it.result.subLocationId
                                )
                                isDeposition.postValue(nav?.haveDeposition ?: it.result.isDeposition)
                                idDeposition.postValue(nav?.idDeposition ?: it.result.subLocationId)
                                createTitleLocation(userAssignment = it.result)
                                _queueCarFleetState.emit(QueueCarFleetState.GetUserInfoSuccess)
                            }
                            GetUserByIdForAssignmentState.UserNotFound -> {
                                _queueCarFleetState.emit(
                                    QueueCarFleetState.FailedGetUser(
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
                _subLocationName.value = subLocationName ?: EMPTY_STRING
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
            if (!mCountCacheCarFleet.isInit) {
                getCount.invoke(
                    subLocationId = mUserRoleInfo.subLocationId,
                    locationId = mUserRoleInfo.locationId
                )
                    .catch { cause ->
                        _queueCarFleetState.emit(
                            QueueCarFleetState.FailedGetCounter(
                                message = cause.message ?: ERROR_MESSAGE_UNKNOWN
                            )
                        )
                    }
                    .collect {
                        when (it) {
                            is GetCountState.Success -> {
                                it.countResult.let { result ->
                                    mCountCacheCarFleet = CountCacheCarFleet(
                                        isInit = true,
                                        stock = result.stock,
                                        request = result.request,
                                        ritase = result.ritase,
                                        depositionStock = result.deposition
                                    )
                                    counterLiveData.postValue(mCountCacheCarFleet)
                                }
                            }
                        }
                    }
            }
        }
    }

    fun updateRequestCount(count: Long) {
        if (count >= RequestCarFleetDialogViewModel.MINIMUM_COUNTER_VALUE) {
            mCountCacheCarFleet.request = count
            counterLiveData.value = mCountCacheCarFleet
        }
    }

    fun departFleet(
        carFleetItem: CarFleetItem,
        isWithPassenger: Boolean = false,
        queueId: String = ""
    ) {
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
            _queueCarFleetState.emit(QueueCarFleetState.ProgressDepartCarFleet)
            departFleet.invoke(
                locationId = mUserRoleInfo.locationId,
                subLocationId = mUserRoleInfo.subLocationId,
                fleetNumber = carFleetItem.name,
                isWithPassenger = isWithPassenger,
                departFleetItems = listOf(carFleetItem.id),
                queueNumber = queueId
            )
                .flowOn(Dispatchers.Main)
                .catch { error ->
                    _queueCarFleetState.emit(
                        QueueCarFleetState.FailedDepart(
                            error.message ?: ERROR_MESSAGE_UNKNOWN
                        )
                    )
                }
                .collectLatest {
                    when (it) {
                        is DepartFleetState.Success -> {
                            _queueCarFleetState.emit(
                                QueueCarFleetState.SuccessDepartCarFleet(
                                    it.fleetDepartResult.taxiNo,
                                    isWithPassenger
                                )
                            )

                            delay(100)
                            mCountCacheCarFleet.stock -= 1
                            if (mCountCacheCarFleet.stock < 0)
                                mCountCacheCarFleet.stock = 0
                            if (isWithPassenger)
                                mCountCacheCarFleet.ritase += 1

                            counterLiveData.postValue(mCountCacheCarFleet)
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
            checkStateList()
        }
    }

    fun showRecordRitase(carFleetItem: CarFleetItem, queueId: String) {
        viewModelScope.launch {
            _queueCarFleetState.emit(
                QueueCarFleetState.RecordRitaseToDepart(
                    carFleetItem,
                    mUserRoleInfo.locationId,
                    mUserRoleInfo.subLocationId,
                    queueId
                )
            )
        }
    }

    fun requestDepart(carFleetItem: CarFleetItem) {
        viewModelScope.launch {
            delay(50)
            _queueCarFleetState.emit(
                QueueCarFleetState.RequestDepartCarFleet(
                    carFleetItem,
                    mUserRoleInfo.locationId,
                    mUserRoleInfo.subLocationId
                )
            )
        }
    }

    fun onErrorFromDialog(throwable: Throwable) {
        viewModelScope.launch {
            if(throwable.message != FragmentDepartCarFleetDialog.DISMISS) {
                _queueCarFleetState.emit(QueueCarFleetState.FailedGetQueueCar(throwable))
            }
            checkStateList()
        }
    }

    fun searchFleet() {
        viewModelScope.launch {
            _queueCarFleetState.emit(
                QueueCarFleetState.SearchCarFleet(
                    subLocationId = mUserRoleInfo.subLocationId,
                    list = _fleetItems
                )
            )
        }
    }

    fun showRequestFleet() {
        viewModelScope.launch {
            _queueCarFleetState.emit(QueueCarFleetState.ShowRequestCarFleet(subLocationId = mUserRoleInfo.subLocationId))
        }
    }

    fun addFleet() {
        viewModelScope.launch {
            _queueCarFleetState.emit(QueueCarFleetState.AddCarFleet(subLocationId = mUserRoleInfo.subLocationId))
            delay(200)
            _queueCarFleetState.emit(QueueCarFleetState.AddCarFleet(subLocationId = mUserRoleInfo.subLocationId))
            delay(200)
            _queueCarFleetState.emit(QueueCarFleetState.AddCarFleet(subLocationId = mUserRoleInfo.subLocationId))
        }
    }

    fun stateIdle() {
        viewModelScope.launch {
            _queueCarFleetState.emit(QueueCarFleetState.Idle)
        }
    }

    fun addSuccess(carFleetItem: CarFleetItem?) {
        if (carFleetItem != null) {
            mCountCacheCarFleet.stock++
            calculateRequestAfterAddStock()
            counterLiveData.value = mCountCacheCarFleet
            viewModelScope.launch {
                _fleetItems.add(carFleetItem)
                _queueCarFleetState.emit(
                    QueueCarFleetState.GetListSuccess(
                        _fleetItems.toList()
                    )
                )
            }
        }
    }

    private fun calculateRequestAfterAddStock() {
        if (mCountCacheCarFleet.request > ZERO) {
            mCountCacheCarFleet.request--
        }
    }

    fun showSearchQueue(
        carFleetItem: CarFleetItem,
        currentQueueId: String,
        locationId: Long,
        subLocationId: Long
    ) {
        viewModelScope.launch {
            _queueCarFleetState.emit(
                QueueCarFleetState.SearchQueueToDepartCar(
                    carFleetItem,
                    locationId,
                    subLocationId,
                    currentQueueId
                )
            )
        }
    }

    fun getFleetList() {
        viewModelScope.launch {
            _queueCarFleetState.emit(QueueCarFleetState.ProgressGetCarFleetList)
            if (_fleetItems.isNotEmpty()) {
                _queueCarFleetState.emit(QueueCarFleetState.GetListSuccess(_fleetItems.toList()))
            } else {
                _getFleet.invoke(mUserRoleInfo.subLocationId)
                    .flowOn(Dispatchers.Main)
                    .catch { cause: Throwable ->
                        _queueCarFleetState.emit(QueueCarFleetState.FailedGetList(cause))
                        _queueCarFleetState.emit(QueueCarFleetState.GetListEmpty)
                    }
                    .collect {
                        when (it) {
                            GetListFleetState.EmptyResult -> {
                                _queueCarFleetState.emit(QueueCarFleetState.GetListEmpty)
                            }

                            is GetListFleetState.Success -> {
                                it.list.forEach { item ->
                                    _fleetItems.add(
                                        CarFleetItem(
                                            id = item.fleetId,
                                            name = item.fleetName,
                                            arriveAt = item.arriveAt.convertCreateAtValue()
                                        )
                                    )
                                }
                                _queueCarFleetState.emit(
                                    QueueCarFleetState.GetListSuccess(
                                        _fleetItems.toList()
                                    )
                                )
                            }
                        }
                    }
            }
        }
    }

    private suspend fun checkStateList() {
        _queueCarFleetState.emit(
            if (_fleetItems.isEmpty()) {
                QueueCarFleetState.GetListEmpty
            } else {
                QueueCarFleetState.GetListSuccess(
                    _fleetItems.toList()
                )
            }
        )
    }

    fun refresh() {
        mCountCacheCarFleet = CountCacheCarFleet()
        _fleetItems.clear()
        init()
    }

    fun goToQrCodeScreen() {
        viewModelScope.launch {
            _queueCarFleetState.emit(
                QueueCarFleetState.GoToQrCodeScreen(
                    locationId = mUserRoleInfo.locationId,
                    subLocationId = mUserRoleInfo.subLocationId,
                    titleLocation = "${_locationName.value} ${_subLocationName.value}"
                )
            )
        }
    }

    fun goToDepositionScreen() {
        viewModelScope.launch {
            _queueCarFleetState.emit(
                QueueCarFleetState.GotoDepositionScreen(
                    title = titleLocation.value ?: EMPTY_STRING,
                    subLocationId = mUserRoleInfo.subLocationId,
                    depositionStock = counterLiveData.value?.depositionStock ?: 0,
                    idDeposition = idDeposition.value ?: 0
                )
            )
        }
    }
}