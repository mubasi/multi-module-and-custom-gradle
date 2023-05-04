package id.bluebird.vsm.feature.airport_fleet.main

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.core.extensions.StringExtensions.convertCreateAtValue
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.fleet.DepartFleetState
import id.bluebird.vsm.domain.fleet.GetCountState
import id.bluebird.vsm.domain.fleet.GetListFleetState
import id.bluebird.vsm.domain.fleet.domain.cases.DepartFleet
import id.bluebird.vsm.domain.fleet.domain.cases.GetCount
import id.bluebird.vsm.domain.fleet.domain.cases.GetListFleet
import id.bluebird.vsm.domain.fleet.model.FleetItemResult
import id.bluebird.vsm.domain.user.GetUserAssignmentState
import id.bluebird.vsm.domain.user.domain.intercator.GetUserAssignment
import id.bluebird.vsm.feature.airport_fleet.main.model.FleetItemCar
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
    private val getCount: GetCount,
    private val getStockBySubLocation: GetListFleet,
    private val dispatchFleet: DepartFleet,
) : ViewModel() {

    companion object {
        const val EMPTY_STRING = ""
        const val ENABLE_VALUE_GREATER = 0
        const val MINIMUM_COUNTER_VALUE = 1
        const val DEFAULT_STATUS = 2L
        const val REQUEST_ID = 0L
        const val PAGE_SIZE = 1000
        const val WRONG_LOCATION = "Wrong location"
        const val DELAY = 200L
        const val CANCEL_VALUE = 0L
    }

    private val _state: MutableSharedFlow<FleetNonApshState> = MutableSharedFlow()
    val state = _state.asSharedFlow()
    private val _title: MutableLiveData<String> = MutableLiveData()
    val title: LiveData<String> = _title
    private val _fleetLiveData: MutableLiveData<List<FleetItemCar>> = MutableLiveData()
    val fleetLiveData: LiveData<List<FleetItemCar>> = _fleetLiveData
    private val _emptyFleetList: MutableLiveData<EmptyType> = MutableLiveData()
    val emptyFleetList: LiveData<EmptyType> = _emptyFleetList
    private var _linkedHashMap: LinkedHashMap<String, FleetItemCar> = LinkedHashMap()
    private val _selectedCarMap: HashMap<String, FleetItemCar> = HashMap()
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
    var isPerimeter: Boolean = false
    var isWings: Boolean = false
    private var nameSubLocation: String = EMPTY_STRING
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
    fun getNameSubLocation(): String = nameSubLocation

    @VisibleForTesting
    fun runGetCounter(status: Boolean) = getCounter(status)

    @VisibleForTesting
    fun getlinkedHashMap(): LinkedHashMap<String, FleetItemCar> = _linkedHashMap

    @VisibleForTesting
    fun setLinkedHashMap(result: ArrayList<FleetItemCar>) {
        result.map {
            _linkedHashMap[it.name] = it
        }

    }

    @VisibleForTesting
    fun setIsPerimeter(status: Boolean) {
        isPerimeter = status
    }

    @VisibleForTesting
    fun setIsWing(status: Boolean) {
        isWings = status
    }

    @VisibleForTesting
    fun setSubLocation(status: Long) {
        idSubLocation = status
    }

    @VisibleForTesting
    fun setNameSubLocation(status: String) {
        nameSubLocation = status
    }

    @VisibleForTesting
    fun setFleetList(result: ArrayList<FleetItemCar>) {
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
    fun setSelectedCarMap(result: ArrayList<FleetItemCar>) {
        result.forEach {
            _selectedCarMap[it.name] = it
        }
    }

    @VisibleForTesting
    fun getSelectedCarMap(fleetNumber: String): FleetItemCar? {
        return _selectedCarMap[fleetNumber]
    }

    @VisibleForTesting
    fun getListSelectedCarMap(): HashMap<String, FleetItemCar> {
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
                nav?.locationName ?: EMPTY_STRING,
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
        nameSubLocation = name
        isPerimeter = perimeter
        isWings = wings
        _selectedCarMap.clear()
        _selectedFleetCounter.postValue(0)
        createTitle(name)
        getCounter(true)
    }

    private fun getCounter(refreshStock: Boolean) {
        viewModelScope.launch {
            delay(400)
            getCount.invoke(idSubLocation, UserUtils.getLocationId())
                .catch { cause ->
                    _state.emit(FleetNonApshState.OnError(cause))
                }
                .collect {
                    when (it) {
                        is GetCountState.Success -> {
                            val temp = it.countResult
                            val tempCountCache = CountCache(
                                temp.stock, temp.request, temp.ritase, temp.request
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
            )
                .catch { cause ->
                    setEmptyList()
                    _state.emit(FleetNonApshState.OnError(cause))
                }
                .collect { result ->
                    when (result) {
                        is GetListFleetState.Success -> {
                            setLinkedHasMap(result.list)
                            _state.emit(FleetNonApshState.Idle)
                        }
                        GetListFleetState.EmptyResult -> {
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

    private fun getUserAssignment() {
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
                            if (it.result.isNotEmpty()) {
                                val item = it.result[0]
                                init(
                                    item.subLocationId,
                                    item.locationName,
                                    item.isDeposition,
                                    item.isWings
                                )
                            } else {
                                setEmptyList()
                                _state.emit(FleetNonApshState.OnEmptyData)
                            }
                        }
                    }
                }
        }
    }

    private fun setLinkedHasMap(result: List<FleetItemResult>) {
        result.map { item ->
            val item = FleetItemCar(
                id = item.fleetId,
                name = item.fleetName,
                arriveAt = item.arriveAt.convertCreateAtValue()
            )
            _linkedHashMap[item.name] = item
        }
        _fleetLiveData.postValue(_linkedHashMap.values.toList())
    }

    fun intentToAddFleetPage() {
        viewModelScope.launch {
            _state.emit(
                FleetNonApshState.IntentToAddFleet(
                    isPerimeter = UserUtils.getFleetTypeId() == DEFAULT_STATUS && isPerimeter,
                    subLocationId = idSubLocation,
                    isWing = isWings
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
                    requestToId = REQUEST_ID,
                    subLocationId = idSubLocation,
                    subLocationName = nameSubLocation
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
            if (isPerimeter)
                EmptyType.Perimeter
            else EmptyType.Terminal
        )
    }

    fun selectFleet(fleetAssignment: FleetItemCar) {
        fleetAssignment.isSelected = true
        _selectedCarMap[fleetAssignment.name] = fleetAssignment
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
            if (isPerimeter) {
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

    private fun selectCarFormat(): ArrayList<FleetItemCar> {
        val result: ArrayList<FleetItemCar> = ArrayList()
        result.addAll(_selectedCarMap.values)
        return result
    }


    private fun dispatchFleetToServer(withPassenger: Boolean = false) {
        viewModelScope.launch {
            val itemFleet = _selectedCarMap.values.first()
//            val dispatchFleetModel = DispatchFleetModel(
//                locationId = UserUtils.getLocationId(),
//                subLocationId = idSubLocation,
//                isPerimeter = isPerimeter,
//                withPassenger = withPassenger,
//                isArrived = isArrivedFleet,
//                fleetsAssignment = getListStockId()
//            )
            dispatchFleet.invoke(
                locationId = UserUtils.getLocationId(),
                subLocationId = idSubLocation,
                fleetNumber = itemFleet.name,
                isWithPassenger = withPassenger,
                departFleetItems = getListStockId(),
                queueNumber = itemFleet.name
            )
                .catch { cause ->
                    _state.emit(FleetNonApshState.OnError(cause))
                }
                .collect {
                    when (it) {
//                        is DepartFleetState.SuccessArrived -> {
//                            val getMessage = getMessage()
//                            updateStockFromList(StatusUpdate.ADD)
//                            updateFleetSuccessArrived(it.mapStockIds)
//                            clearCacheSelected()
//                            delay(DELAY)
//                            _state.emit(
//                                FleetNonApshState.SuccessArrived(
//                                    getMessage,
//                                    isStatusArrived = isArrivedFleet,
//                                    isWithPassenger = withPassenger
//                                )
//                            )
//
//                        }
//                        is DispatchFleetState.SuccessDispatchFleet -> {
//                            val getMessage = getMessage()
//                            updateStockFromList(StatusUpdate.DEFICIENT)
//                            updateRitaseFromList(StatusUpdate.ADD)
//                            removeUpdateData()
//                            delay(DELAY)
//                            _state.emit(
//                                FleetNonApshState.SuccessDispatchFleet(
//                                    getMessage,
//                                    isNonTerminal = isArrivedFleet,
//                                    isWithPassenger = withPassenger
//                                )
//                            )
//                        }
//                        DispatchFleetState.WrongDispatchLocation -> {
//                            _state.emit(FleetNonApshState.OnError(Throwable(message = WRONG_LOCATION)))
//                        }
                        is DepartFleetState.Success -> {
                            val getMessage = getMessage()
                            updateStockFromList(StatusUpdate.DEFICIENT)
                            updateRitaseFromList(StatusUpdate.ADD)
                            removeUpdateData()
                            delay(DELAY)
                            _state.emit(
                                FleetNonApshState.SuccessDispatchFleet(
                                    getMessage,
                                    isNonTerminal = false,
                                    isWithPassenger = withPassenger
                                )
                            )
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
            _selectedCarMap.values.first().name
        }
    }

    fun updateFleetSuccessArrived(fleetAssignments: HashMap<String, Long>) {
        val temp = _linkedHashMap
        fleetAssignments.forEach { map ->
            _linkedHashMap[map.key]?.copy()?.let {
                it.id = map.value
                it.status = STATUS.ARRIVED.name
                it.isSelected = false
                temp[it.name] = it
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
            _linkedHashMap[it.name]?.isSelected = false
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
            stockIdList.add(it.id)
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

    fun setFleetSelected(carAssignment: FleetItemCar, newStatus: Boolean) {
        viewModelScope.launch {
            updateSelectedItem(fleetAssignment = carAssignment, newStatus = newStatus)
            displayListWithFilter(
                item = carAssignment.copy(),
                newStatus = newStatus
            )
            notifySelectedCarCounter()
            delay(DELAY)
            _linkedHashMap[carAssignment.name]?.isSelected = newStatus
        }
    }

    private fun updateSelectedItem(fleetAssignment: FleetItemCar, newStatus: Boolean) {
        if (newStatus) {
            _selectedCarMap[fleetAssignment.name] = fleetAssignment
        } else {
            _selectedCarMap.remove(fleetAssignment.name)
        }
        notifySelectedCarCounter()
    }

    private fun displayListWithFilter(
        item: FleetItemCar,
        newStatus: Boolean
    ) {
        val filter = getListFleet(item.status)
        val tempList: MutableList<FleetItemCar> = mutableListOf()
        filter.forEachIndexed { _, fleetAssignment ->
            val fleet = fleetAssignment.copy()
            if (fleetAssignment.name == item.name) {
                fleet.isSelected = newStatus
            }
            tempList.add(fleet)
        }
        _fleetLiveData.postValue(tempList)
    }

    private fun getListFleet(status: String): List<FleetItemCar> {
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
            _selectedCarMap.forEach {
                _linkedHashMap.remove(it.key)
            }
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