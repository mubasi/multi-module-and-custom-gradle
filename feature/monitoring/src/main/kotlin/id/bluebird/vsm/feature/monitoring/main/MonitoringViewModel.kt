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
): ViewModel() {

    private val _monitoringState: MutableSharedFlow<MonitoringState> = MutableSharedFlow()
    val monitoringState = _monitoringState.asSharedFlow()
    val notificationVisibility = MutableLiveData(true)
    private val isPrivilegedUser: Boolean by lazy {
        when(UserUtils.getPrivilege()) {
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
                    when(it) {
                        is MonitoringResultState.Error -> _monitoringState.emit(MonitoringState.OnFailedGetList)
                        is MonitoringResultState.Success -> {
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
                            _monitoringState.emit(MonitoringState.OnSuccessGetList(data))
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
}