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

    private val _monitoringState: MutableSharedFlow<MonitoringState> = MutableSharedFlow()
    val monitoringState = _monitoringState.asSharedFlow()
    val notificationVisibility = MutableLiveData(true)
    val listLocation: MutableList<MonitoringModel> = mutableListOf()
    var params: MutableLiveData<String> = MutableLiveData("")
    private val isPrivilegedUser: Boolean by lazy {
        when (UserUtils.getPrivilege()) {
            UserUtils.SVP, UserUtils.OFFICER -> false
            else -> true
        }
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
                            listLocation.addAll(data)
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
                if (item.locationName.toLowerCase().contains(params.value?.toLowerCase() ?: "")) {
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
}