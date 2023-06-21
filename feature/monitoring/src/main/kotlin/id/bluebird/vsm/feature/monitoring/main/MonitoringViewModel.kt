package id.bluebird.vsm.feature.monitoring.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.fleet.MonitoringResultState
import id.bluebird.vsm.domain.fleet.domain.cases.Monitoring
import id.bluebird.vsm.feature.monitoring.model.MonitoringModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MonitoringViewModel(
    private val monitoringUseCases: Monitoring
) : ViewModel() {

    companion object {
        const val EMPTY_STRING = ""
        const val ALL = "Semua"
        const val DEPOSITION = "Pengendapan"
        const val LOBBY = "Lobi"
    }

    private val _monitoringState: MutableSharedFlow<MonitoringState> = MutableSharedFlow()
    val monitoringState = _monitoringState.asSharedFlow()
    val notificationVisibility = MutableLiveData(true)
    val listLocation: MutableList<MonitoringModel> = mutableListOf()
    var params: MutableLiveData<String> = MutableLiveData("")
    var sortIsDesc: Boolean = false
    var activeColumnSort: MutableLiveData<ActiveSort> = MutableLiveData(ActiveSort.FleetNumber)
    private val isPrivilegedUser: Boolean by lazy {
        when (UserUtils.getPrivilege()) {
            UserUtils.SVP, UserUtils.OFFICER -> false
            else -> true
        }
    }
    private var filterStatus : FilterStatus = FilterStatus.ALL
    var titleStatusFilter : MutableLiveData<String> = MutableLiveData(ALL)
    enum class FilterStatus {
        ALL, DEPOSITION, LOBBY
    }

    enum class ActiveSort {
       LocationName, FleetNumber, FleetPassenger, TotalRitase, TotalQueueFleet, TotalPassengerQueue, RequestFleet, Deposition
    }

    fun init() {
        viewModelScope.launch {
            _monitoringState.emit(MonitoringState.OnProgressGetList)
            monitoringUseCases
                .invoke()
                .catch {
                    _monitoringState.emit(MonitoringState.OnFailedGetList)
                }
                .collect {
                    when (it) {
                        is MonitoringResultState.Error -> _monitoringState.emit(MonitoringState.OnFailedGetList)
                        is MonitoringResultState.Success -> {
                            listLocation.clear()
                            val data = it.data.map { result ->
                                MonitoringModel(
                                    subLocationId = result.subLocationId,
                                    locationName = result.locationName,
                                    subLocationName = result.subLocationName,
                                    isDeposition = result.isDeposition,
                                    fleetCount = result.queueFleet,
                                    queueCount = result.queuePassenger,
                                    totalFleetCount = result.totalQueueFleet,
                                    totalQueueCount = result.totalQueuePassenger,
                                    totalRitase = result.totalRitase,
                                    fleetRequest = result.request,
                                    buffer = result.buffer,
                                    editableBuffer = isPrivilegedUser
                                )
                            }
                            val resultSort = orderingData(data)
                            listLocation.addAll(resultSort)
                            _monitoringState.emit(
                                MonitoringState.OnSuccessGetList(
                                    resultFilterLocation(resultSort)
                                )
                            )
                        }
                    }
                }
        }
    }

    private fun orderingData(data : List<MonitoringModel>) : List<MonitoringModel> {
        return when (activeColumnSort.value) {
            ActiveSort.LocationName -> {
                sortLocationName(data)
            }
            ActiveSort.FleetPassenger -> {
                sortQueueCount(data)
            }
            ActiveSort.TotalRitase -> {
                sortTotalRitase(data)
            }
            ActiveSort.TotalQueueFleet -> {
                sortTotalFleetCount(data)
            }
            ActiveSort.TotalPassengerQueue -> {
                sortTotalQueueCount(data)
            }
            ActiveSort.RequestFleet -> {
                sortFleetRequest(data)
            }
            ActiveSort.Deposition -> {
                sortBuffer(data)
            }
            else -> {
                sortFleetCount(data)
            }
        }
    }

    private fun sortLocationName(data : List<MonitoringModel>) : List<MonitoringModel> {
        return if(sortIsDesc) {
            data.sortedByDescending { item -> item.locationName }
        } else {
            data.sortedBy { item -> item.locationName }
        }
    }

    private fun sortQueueCount(data : List<MonitoringModel>) : List<MonitoringModel> {
        return if(sortIsDesc) {
            data.sortedByDescending { item -> item.queueCount }
        } else {
            data.sortedBy { item -> item.queueCount }
        }
    }

    private fun sortTotalRitase(data : List<MonitoringModel>) : List<MonitoringModel> {
        return if(sortIsDesc) {
            data.sortedByDescending { item -> item.totalRitase }
        } else {
            data.sortedBy { item -> item.totalRitase }
        }
    }

    private fun sortTotalFleetCount(data : List<MonitoringModel>) : List<MonitoringModel> {
        return if(sortIsDesc) {
            data.sortedByDescending { item -> item.totalFleetCount }
        } else {
            data.sortedBy { item -> item.totalFleetCount }
        }
    }

    private fun sortTotalQueueCount(data : List<MonitoringModel>) : List<MonitoringModel> {
        return if(sortIsDesc) {
            data.sortedByDescending { item -> item.totalQueueCount }
        } else {
            data.sortedBy { item -> item.totalQueueCount }
        }
    }

    private fun sortFleetRequest(data : List<MonitoringModel>) : List<MonitoringModel> {
        return if(sortIsDesc) {
            data.sortedByDescending { item -> item.fleetRequest }
        } else {
            data.sortedBy { item -> item.fleetRequest }
        }
    }
    private fun sortFleetCount(data : List<MonitoringModel>) : List<MonitoringModel> {
        val resultData = resultFilterStatus(data)
        return if(sortIsDesc) {
            resultData.sortedByDescending { item -> item.fleetCount }
        } else {
            resultData.sortedBy { item -> item.fleetCount }
        }
    }

    private fun sortBuffer(data : List<MonitoringModel>) : List<MonitoringModel> {
        val resultData = resultFilterStatus(data)
        return if(sortIsDesc) {
            resultData.sortedByDescending { item -> item.buffer }
        } else {
            resultData.sortedBy { item -> item.buffer }
        }
    }

    private fun resultFilterStatus(data : List<MonitoringModel>) : List<MonitoringModel> {
        return if(filterStatus != FilterStatus.ALL) {
            data.filter { it.isDeposition == (filterStatus == FilterStatus.DEPOSITION)}
       } else {
           data
       }
    }

    fun toggleNotificationVisibility() {
        notificationVisibility.postValue(!(notificationVisibility.value ?: true))
    }

    fun onDialogSaveResult(isSuccess: Boolean, failedMessage: String?) {
        viewModelScope.launch {
            if (isSuccess)
                _monitoringState.emit(MonitoringState.OnSuccessSaveBuffer)
            else
                _monitoringState.emit(MonitoringState.OnFailedSaveBuffer(failedMessage ?: "Failed"))
        }
    }

    fun editBuffer(model: MonitoringModel?) {
        if (model == null || !isPrivilegedUser)
            return

        viewModelScope.launch {
            _monitoringState.emit(MonitoringState.RequestEditBuffer(model))
        }
    }

    fun searchScreen() {
        viewModelScope.launch {
            _monitoringState.emit(MonitoringState.SearchScreen)
        }
    }

    fun backSearchScreen() {
        viewModelScope.launch {
            _monitoringState.emit(MonitoringState.BackSearchScreen)
        }
    }

    fun filterLocation() {
        viewModelScope.launch {
            if (resultFilterLocation(listLocation.toList()).isEmpty()) {
                _monitoringState.emit(MonitoringState.ErrorFilter)
            } else {
                _monitoringState.emit(MonitoringState.FilterLocation(resultFilterLocation(listLocation.toList())))
            }
        }
    }

    private fun resultFilterLocation(datas : List<MonitoringModel>): List<MonitoringModel> {
        val filteredList: MutableList<MonitoringModel> = mutableListOf()
        if (params.value.isNullOrEmpty()) {
            filteredList.addAll(datas)
        } else {
            for (item in datas) {
                if (item.locationName.toLowerCase().contains(params.value?.toLowerCase() ?: EMPTY_STRING)) {
                    filteredList.add(item)
                }
            }
        }
        return filteredList
    }

    fun clearSearch() {
        params.value = ""
        filterLocation()
    }

    fun changeStatusOrder(sort : ActiveSort, isDesc : Boolean) {
        sortIsDesc = !isDesc
        activeColumnSort.value = sort
        setResultFilter()
    }

    private fun setResultFilter() {
        viewModelScope.launch {
            val resultSort = orderingData(listLocation)
            _monitoringState.emit(
                MonitoringState.OnSuccessGetList(
                    resultFilterLocation(resultSort)
                )
            )
        }
    }
    fun openDialogFilter() {
        viewModelScope.launch {
            _monitoringState.emit(
                MonitoringState.OpenDialogFilter(
                    filterStatus
                )
            )
        }
    }
    fun updateFilterStatus(result : FilterStatus) {
        filterStatus = result
        getStatusFilterToString()
        setResultFilter()
    }

    private fun getStatusFilterToString() {
        when(filterStatus) {
            FilterStatus.DEPOSITION -> {
                titleStatusFilter.value = DEPOSITION
            }
            FilterStatus.LOBBY -> {
                titleStatusFilter.value = LOBBY
            }
            else -> {
                titleStatusFilter.value = ALL
            }
        }
    }

}