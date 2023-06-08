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
                                    resultFilterLocation()
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
       return if(sortIsDesc) {
            data.sortedByDescending { item -> item.fleetCount }
        } else {
            data.sortedBy { item -> item.fleetCount }
        }
    }

    private fun sortBuffer(data : List<MonitoringModel>) : List<MonitoringModel> {
        return if(sortIsDesc) {
            data.sortedByDescending { item -> item.buffer }
        } else {
            data.sortedBy { item -> item.buffer }
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
            if (resultFilterLocation().isEmpty()) {
                _monitoringState.emit(MonitoringState.ErrorFilter)
            } else {
                _monitoringState.emit(MonitoringState.FilterLocation(resultFilterLocation()))
            }
        }
    }

    private fun resultFilterLocation(): List<MonitoringModel> {
        val filteredList: MutableList<MonitoringModel> = mutableListOf()
        if (params.value.isNullOrEmpty()) {
            filteredList.addAll(listLocation)
        } else {
            for (item in listLocation) {
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
        viewModelScope.launch {
            sortIsDesc = !isDesc
            activeColumnSort.value = sort
            val resultSort = orderingData(listLocation)
            listLocation.clear()
            listLocation.addAll(resultSort)
            _monitoringState.emit(
                MonitoringState.OnSuccessGetList(
                    resultFilterLocation()
                )
            )
        }
    }

}