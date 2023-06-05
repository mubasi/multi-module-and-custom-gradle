package id.bluebird.vsm.feature.airport_fleet.main

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.airport_assignment.DispatchFleetAirportState
import id.bluebird.vsm.domain.airport_assignment.GetListFleetTerminalDepartState
import id.bluebird.vsm.domain.airport_assignment.GetSubLocationStockCountDepartState
import id.bluebird.vsm.domain.airport_assignment.domain.cases.DispatchFleetAirport
import id.bluebird.vsm.domain.airport_assignment.domain.cases.GetListFleetTerminal
import id.bluebird.vsm.domain.airport_assignment.domain.cases.GetSubLocationStockCountDepart
import id.bluebird.vsm.domain.airport_assignment.model.FleetItemDepartModel
import id.bluebird.vsm.domain.user.GetUserAssignmentState
import id.bluebird.vsm.domain.user.domain.intercator.GetUserAssignment
import id.bluebird.vsm.feature.airport_fleet.main.model.AssignmentCarCache
import id.bluebird.vsm.domain.airport_assignment.model.DispatchFleetModel
import id.bluebird.vsm.feature.airport_fleet.main.model.STATUS
import id.bluebird.vsm.fleet_non_apsh.main.model.CountCache
import id.bluebird.vsm.feature.airport_fleet.utils.EmptyType
import id.bluebird.vsm.feature.select_location.LocationNavigationTemporary
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch

class FleetNonApshViewModel(
    private val getUserAssignment: GetUserAssignment,
    private val getCount: GetSubLocationStockCountDepart,
    private val getStockBySubLocation: GetListFleetTerminal,
    private val dispatchFleetAirport: DispatchFleetAirport,
) : ViewModel() {

    companion object {
        const val EMPTY_STRING = ""
        const val ENABLE_VALUE_GREATER = 0
        const val MINIMUM_COUNTER_VALUE = 1
        const val DEFAULT_STATUS = 2L
        const val PAGE_SIZE = 1000
        const val WRONG_LOCATION = "Wrong location"
        const val DELAY = 200L
        const val CANCEL_VALUE = -1L
    }

    private val _state: MutableSharedFlow<FleetNonApshState> = MutableSharedFlow()
    val state = _state.asSharedFlow()
    private val _title: MutableLiveData<String> = MutableLiveData()
    val title: LiveData<String> = _title
    private val _fleetLiveData: MutableLiveData<List<AssignmentCarCache>> = MutableLiveData()
    val fleetLiveData: LiveData<List<AssignmentCarCache>> = _fleetLiveData
    private val _emptyFleetList: MutableLiveData<EmptyType> = MutableLiveData()
    val emptyFleetList: LiveData<EmptyType> = _emptyFleetList
    private var _linkedHashMap: LinkedHashMap<String, AssignmentCarCache> = LinkedHashMap()
    private val _selectedCarMap: HashMap<String, AssignmentCarCache> = HashMap()
    private val _selectedFleetCounter: MutableLiveData<Int> = MutableLiveData()
    val selectFleetCounter: LiveData<Int> = _selectedFleetCounter
    private val _updateButtonFleetItem: MutableLiveData<Boolean> = MutableLiveData(false)
    val updateButtonFleetItem: LiveData<Boolean> = _updateButtonFleetItem
    private val _statusFleetIsArrived: MutableLiveData<String> = MutableLiveData()
    val statusFleetIsArrived: LiveData<String> = _statusFleetIsArrived
    private val _counterLiveData: MutableLiveData<CountCache> = MutableLiveData(
        CountCache(
            stock = 0,
            request = 0,
            ritase = 0,
            requestToPerimeter = 0
        )
    )
    val counterLiveData: LiveData<CountCache> = _counterLiveData
    private var _isPerimeter: MutableLiveData<Boolean> = MutableLiveData(false)
    val isPerimeter: LiveData<Boolean> = _isPerimeter
    private var _isWings: MutableLiveData<Boolean> = MutableLiveData(false)
    val isWings: LiveData<Boolean> = _isWings
    private var idSubLocation: Long = -1
    val carSearch: MutableLiveData<String> = MutableLiveData()
    val searchFleetInfo: MutableLiveData<String> = MutableLiveData()
    private var mCountCache = CountCache(
        stock = 0,
        request = 0,
        ritase = 0
    )

    @VisibleForTesting
    fun getIdSubLocation(): Long = idSubLocation

    @VisibleForTesting
    fun getNameSubLocation(): String = title.value ?: EMPTY_STRING

    @VisibleForTesting
    fun runGetCounter(status: Boolean) = getCounter(status)

    @VisibleForTesting
    fun getlinkedHashMap(): LinkedHashMap<String, AssignmentCarCache> = _linkedHashMap

    @VisibleForTesting
    fun setLinkedHashMap(result: ArrayList<AssignmentCarCache>) {
        result.map {
            _linkedHashMap[it.fleetNumber] = it
        }

    }

    @VisibleForTesting
    fun setIsPerimeter(status: Boolean) {
        _isPerimeter.postValue(status)
    }

    @VisibleForTesting
    fun setIsWing(status: Boolean) {
        _isWings.postValue(status)
    }

    @VisibleForTesting
    fun setSubLocation(status: Long) {
        idSubLocation = status
    }

    @VisibleForTesting
    fun setNameSubLocation(status: String) {
        _title.postValue(status)
    }

    @VisibleForTesting
    fun setFleetList(result: ArrayList<AssignmentCarCache>) {
        _fleetLiveData.postValue(
            result
        )
    }

    @VisibleForTesting
    fun setCounter(result: CountCache) {
        mCountCache = result
        _counterLiveData.postValue(mCountCache)
    }

    @VisibleForTesting
    fun getCounter(): CountCache = mCountCache

    @VisibleForTesting
    fun setSelectedCarMap(result: ArrayList<AssignmentCarCache>) {
        result.forEach {
            _selectedCarMap[it.fleetNumber] = it
        }
    }

    @VisibleForTesting
    fun getSelectedCarMap(fleetNumber: String): AssignmentCarCache? {
        return _selectedCarMap[fleetNumber]
    }

    @VisibleForTesting
    fun getListSelectedCarMap(): HashMap<String, AssignmentCarCache> {
        return _selectedCarMap
    }

    fun initialize() {
        if (LocationNavigationTemporary.isLocationNavAvailable()
                .not() && UserUtils.isUserOfficer().not()
        ) {
            viewModelScope.launch {
                _state.emit(FleetNonApshState.ProgressHolder)
                delay(400)
                _state.emit(FleetNonApshState.ToSelectLocation)
            }
        } else if (LocationNavigationTemporary.isLocationNavAvailable()
            && UserUtils.isUserOfficer().not()
        ) {
            val nav = LocationNavigationTemporary.getLocationNav()
            init(
                nav?.subLocationId ?: -1,
                nav?.subLocationName ?: EMPTY_STRING,
                nav?.isPerimeter ?: false,
                nav?.isWing ?: false
            )
        } else {
            getUserAssignment()
        }
    }

    fun init(
        subLocationId: Long,
        name: String,
        perimeter: Boolean,
        wings: Boolean
    ) {
        idSubLocation = subLocationId
        _isPerimeter.postValue(perimeter)
        _isWings.postValue(wings)
        _selectedCarMap.clear()
        _selectedFleetCounter.postValue(0)
        createTitle(name)
        getCounter(true)
    }

    private fun getCounter(refreshStock: Boolean) {
        viewModelScope.launch {
            delay(400)
            getCount.invoke(idSubLocation, UserUtils.getLocationId(), 0L)
                .catch { cause ->
                    _state.emit(FleetNonApshState.OnError(cause))
                }
                .collect {
                    when (it) {
                        is GetSubLocationStockCountDepartState.Success -> {
                            val temp = it.result
                            val tempCountCache = CountCache(
                                temp.stock, temp.request, temp.ritase, 0L
                            )
                            mCountCache = tempCountCache
                            _counterLiveData.postValue(tempCountCache)
                            if (refreshStock) {
                                _state.emit(FleetNonApshState.GetCountSuccess)
                            }
                        }
                        else -> {
                            //do nothing
                        }
                    }
                }
        }
    }

    fun getFleetByLocation() {
        viewModelScope.launch {
            getStockBySubLocation.invoke(
                subLocationId = idSubLocation,
                page = 1,
                itemPerPage = PAGE_SIZE
            )
                .catch {
                    setEmptyList()
                    _state.emit(FleetNonApshState.OnEmptyData)
                }
                .collect { item ->
                    when (item) {
                        is GetListFleetTerminalDepartState.Success -> {
                            setLinkedHasMap(item.result)
                            _state.emit(FleetNonApshState.Idle)
                        }
                        GetListFleetTerminalDepartState.EmptyResult -> {
                            setEmptyList()
                            _state.emit(FleetNonApshState.OnEmptyData)
                        }
                    }
                }
        }
    }

    private fun createTitle(result: String) {
        _title.postValue(result)
    }

    fun getUserAssignment() {
        viewModelScope.launch {
            _state.emit(FleetNonApshState.OnProgress)
            getUserAssignment.invoke(UserUtils.getUserId())
                .catch { cause ->
                    _state.emit(FleetNonApshState.OnError(cause))
                }
                .collect {
                    when (it) {
                        GetUserAssignmentState.UserNotFound -> {
                            _state.emit(FleetNonApshState.OnEmptyData)
                        }
                        is GetUserAssignmentState.Success -> {
                            val item = it.result
                            init(
                                item.subLocationId,
                                item.subLocationName,
                                item.isDeposition,
                                item.isWings
                            )
                        }
                    }
                }
        }
    }

    fun setLinkedHasMap(result: List<FleetItemDepartModel>) {
        removeUpdateData()
        result.map {
            val item = AssignmentCarCache(
                fleetNumber = it.taxiNo,
                date = it.createdAt,
                dateAfterConvert = it.createdAt,
                stockId = it.fleetId,
                isTU = it.isTu,
                sequence = it.sequence,
                status = it.status
            )
            _linkedHashMap[item.fleetNumber] = item
        }
        _fleetLiveData.postValue(_linkedHashMap.values.toList())
    }

    fun intentToAddFleetPage() {
        viewModelScope.launch {
            _state.emit(
                FleetNonApshState.IntentToAddFleet(
                    isPerimeter = UserUtils.getFleetTypeId() == DEFAULT_STATUS && isPerimeter.value ?: false,
                    subLocationId = idSubLocation,
                    isWing = isWings.value ?: false
                )
            )
        }
    }

    fun takePicture() {
        viewModelScope.launch {
            _state.emit(
                FleetNonApshState.TakePicture(
                    idSubLocation
                )
            )
        }
    }

    fun dialogRequest() {
        viewModelScope.launch {
            _state.emit(
                FleetNonApshState.DialogRequest(
                    requestToId = UserUtils.getLocationId(),
                    subLocationId = idSubLocation,
                    subLocationName = title.value ?: EMPTY_STRING
                )
            )
        }
    }

    fun updateRequestCount(count: Long) {
        if (count == CANCEL_VALUE) {
            setConditionListFleet()
        } else {
            if (count >= MINIMUM_COUNTER_VALUE) {
                mCountCache.request = count
                _counterLiveData.postValue(mCountCache)
                setConditionListFleet()
            }
        }
    }

    fun setConditionListFleet() {
        viewModelScope.launch {
            if (fleetLiveData.value == null) {
                _state.emit(FleetNonApshState.OnEmptyData)
            } else {
                _state.emit(FleetNonApshState.Idle)
            }
        }
    }

    fun setEmptyList() {
        _emptyFleetList.postValue(
            if (isPerimeter.value == true)
                EmptyType.Perimeter
            else EmptyType.Terminal
        )
    }

    fun selectFleet(fleetAssignment: AssignmentCarCache) {
        fleetAssignment.isSelected = true
        _selectedCarMap[fleetAssignment.fleetNumber] = fleetAssignment
        notifySelectedCarCounter()
        dispatchFleets(fleetAssignment.status)
    }

    fun dispatchSend(isWithPassenger: Boolean?) {
        if (isWithPassenger != null) {
            dispatchFleetToServer(isWithPassenger)
        } else {
            clearCacheSelected()
        }
    }

    fun dispatchMultipleFleet() {
        dispatchFleets()
    }

    private fun dispatchFleets(newStatus: String? = null) {
        viewModelScope.launch {
            val status = newStatus ?: statusFleetIsArrived.value
            if (isPerimeter.value == true) {
                _state.emit(
                    if (status == STATUS.OTW.name) FleetNonApshState.DispatchCar(
                        status == STATUS.OTW.name
                    ) else FleetNonApshState.SendCar(
                        result = selectCarFormat()
                    )
                )
            } else {
                _state.emit(
                    FleetNonApshState.DispatchCar(
                        status == STATUS.OTW.name
                    )
                )
            }
        }
    }

    private fun selectCarFormat(): ArrayList<AssignmentCarCache> {
        val result: ArrayList<AssignmentCarCache> = ArrayList()
        result.addAll(_selectedCarMap.values)
        return result
    }


    private fun dispatchFleetToServer(withPassenger: Boolean = false) {
        viewModelScope.launch {
            val isArrivedFleet = _selectedCarMap.values.first().status == STATUS.ARRIVED.name
            val dispatchFleetModel = DispatchFleetModel(
                locationId = UserUtils.getLocationId(),
                subLocationId = idSubLocation,
                isPerimeter = isPerimeter.value == true,
                withPassenger = withPassenger,
                isArrived = isArrivedFleet,
                fleetsAssignment = getListStockId()
            )
            dispatchFleetAirport.invoke(dispatchFleetModel)
                .catch { cause ->
                    _state.emit(FleetNonApshState.OnError(cause))
                }
                .collect {
                    when (it) {
                        is DispatchFleetAirportState.SuccessArrived -> {
                            val getMessage = getMessage()
                            updateStockFromList(StatusUpdate.ADD)
                            updateFleetSuccessArrived(it.mapStockIds)
                            clearCacheSelected()
                            delay(DELAY)
                            _state.emit(
                                FleetNonApshState.SuccessArrived(
                                    getMessage,
                                    isStatusArrived = isArrivedFleet,
                                    isWithPassenger = withPassenger
                                )
                            )

                        }
                        is DispatchFleetAirportState.SuccessDispatchFleet -> {
                            val getMessage = getMessage()
                            updateStockFromList(StatusUpdate.DEFICIENT)
                            if(withPassenger) {
                                updateRitaseFromList(StatusUpdate.ADD)
                            }
                            removeUpdateData()
                            delay(DELAY)
                            _state.emit(
                                FleetNonApshState.SuccessDispatchFleet(
                                    getMessage,
                                    isNonTerminal = isArrivedFleet,
                                    isWithPassenger = withPassenger
                                )
                            )
                        }
                        DispatchFleetAirportState.WrongDispatchLocation -> {
                            _state.emit(FleetNonApshState.OnError(Throwable(message = WRONG_LOCATION)))
                        }
                    }
                    _selectedCarMap.clear()
                }
        }
    }

    private fun getMessage(): Any {
        return if (_selectedCarMap.size > 1) {
            _selectedCarMap.size
        } else {
            _selectedCarMap.values.first().fleetNumber
        }
    }

    fun updateFleetSuccessArrived(fleetAssignments: HashMap<String, Long>) {
        val temp = _linkedHashMap
        fleetAssignments.forEach { map ->
            _linkedHashMap[map.key]?.copy()?.let {
                it.stockId = map.value
                it.status = STATUS.ARRIVED.name
                it.isSelected = false
                temp[it.fleetNumber] = it
            }
        }
        _fleetLiveData.postValue(temp.values.toList())
    }

    fun updateStockFromList(status: StatusUpdate) {
        val result: Int = _selectedCarMap.size
        val totalStock: Long = mCountCache.stock
        if (status == StatusUpdate.ADD) {
            mCountCache.stock = totalStock + result
        } else if (status == StatusUpdate.DEFICIENT) {
            mCountCache.stock = totalStock - result
        }
        _counterLiveData.postValue(mCountCache)
    }

    fun updateRitaseFromList(status: StatusUpdate) {
        val result = _selectedCarMap.size
        val totalRitase: Long = mCountCache.ritase
        if (status == StatusUpdate.ADD) {
            mCountCache.ritase = totalRitase + result
        } else if (status == StatusUpdate.DEFICIENT) {
            mCountCache.ritase = totalRitase - result
        }
        _counterLiveData.postValue(mCountCache)
    }

    fun clearCacheSelected() {
        _selectedCarMap.values.forEach {
            _linkedHashMap[it.fleetNumber]?.isSelected = false
        }
        _fleetLiveData.postValue(_linkedHashMap.values.toList())
        _selectedCarMap.clear()
        notifySelectedCarCounter()
        _selectedFleetCounter.postValue(0)
        _updateButtonFleetItem.postValue(false)
    }

    private fun getListStockId(): List<Long> {
        val stockIdList: MutableList<Long> = mutableListOf()
        _selectedCarMap.values.forEach {
            stockIdList.add(it.stockId)
        }
        return stockIdList
    }

    private fun notifySelectedCarCounter() {
        val size = _selectedCarMap.size
        _selectedFleetCounter.postValue(size)
        _statusFleetIsArrived.postValue(
            if (size == 0) EMPTY_STRING else _selectedCarMap.values.first().status
        )
    }

    fun setFleetSelected(carAssignment: AssignmentCarCache, newStatus: Boolean) {
        viewModelScope.launch {
            updateSelectedItem(fleetAssignment = carAssignment, newStatus = newStatus)
            displayListWithFilter(
                item = carAssignment.copy(),
                newStatus = newStatus
            )
            notifySelectedCarCounter()
            delay(DELAY)
            _linkedHashMap[carAssignment.fleetNumber]?.isSelected = newStatus
        }
    }

    private fun updateSelectedItem(fleetAssignment: AssignmentCarCache, newStatus: Boolean) {
        if (newStatus) {
            _selectedCarMap[fleetAssignment.fleetNumber] = fleetAssignment
        } else {
            _selectedCarMap.remove(fleetAssignment.fleetNumber)
        }
        notifySelectedCarCounter()
    }

    private fun displayListWithFilter(
        item: AssignmentCarCache,
        newStatus: Boolean
    ) {
        val filter = getListFleet(item.status)
        val tempList: MutableList<AssignmentCarCache> = mutableListOf()
        filter.forEachIndexed { _, fleetAssignment ->
            val fleet = fleetAssignment.copy()
            if (fleetAssignment.fleetNumber == item.fleetNumber) {
                fleet.isSelected = newStatus
            }
            tempList.add(fleet)
        }
        _fleetLiveData.postValue(tempList)
    }

    private fun getListFleet(status: String): List<AssignmentCarCache> {
        return if (_selectedCarMap.isEmpty()) {
            _linkedHashMap.values.toList()
        } else {
            _linkedHashMap.values.toMutableList().filter { it.status == status }
        }
    }

    fun updateListFleetState(isShowFilterMessage: Boolean) {
        if (isShowFilterMessage) {
            _emptyFleetList.value = EmptyType.FilterFleet
        } else {
            setEmptyList()
        }
    }

    fun removeUpdateData() {
        viewModelScope.launch {
            _linkedHashMap.clear()
            _selectedCarMap.clear()
            _fleetLiveData.postValue(_linkedHashMap.values.toList())
            clearCacheSelected()
            delay(DELAY)
        }
    }


    enum class StatusUpdate {
        ADD,
        DEFICIENT
    }

}