package id.bluebird.vsm.feature.airport_fleet.assign_location

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.airport_assignment.AssignFleetTerminalAirportState
import id.bluebird.vsm.domain.airport_assignment.GetSubLocationAirportState
import id.bluebird.vsm.domain.airport_assignment.RitaseFleetTerminalAirportState
import id.bluebird.vsm.domain.airport_assignment.domain.cases.AssignFleetTerminalAirport
import id.bluebird.vsm.domain.airport_assignment.domain.cases.GetSubLocationAirport
import id.bluebird.vsm.domain.airport_assignment.domain.cases.RitaseFleetTerminalAirport
import id.bluebird.vsm.domain.airport_assignment.model.AssignFleetModel
import id.bluebird.vsm.domain.airport_assignment.model.FleetItemDepartModel
import id.bluebird.vsm.feature.airport_fleet.main.model.AssignLocationModel
import id.bluebird.vsm.feature.airport_fleet.main.model.AssignmentCarCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AssignLocationViewModel(
    private val getSubLocationAssigment: GetSubLocationAirport,
    private val ritaseFleetTerminal: RitaseFleetTerminalAirport,
    private val assignFleetTerminal: AssignFleetTerminalAirport,
) : ViewModel() {

    private val _action: MutableSharedFlow<AssignLocationState> =
        MutableSharedFlow()
    val action: SharedFlow<AssignLocationState> = _action.asSharedFlow()
    val selectedLocation: MutableLiveData<AssignLocationModel> = MutableLiveData()
    var selectedCarMap: ArrayList<AssignmentCarCache> = ArrayList()
    val isWing: MutableLiveData<Boolean> = MutableLiveData(false)
    val isPerimeter: MutableLiveData<Boolean> = MutableLiveData()
    private val showWingsChild : Boolean = false
    private var versionCode : Long = -1

    @VisibleForTesting
    fun getVersionCode() : Long = versionCode

    @VisibleForTesting
    fun setSelectedLocation(result : AssignLocationModel) {
        selectedLocation.postValue(result)
    }

    @VisibleForTesting
    fun setListFleet(result : ArrayList<AssignmentCarCache>) {
        selectedCarMap.addAll(result)
    }

    @VisibleForTesting
    fun setIsPerimeter(result : Boolean) {
        isPerimeter.postValue(result)
    }

    fun setupVersionCode(vCode : Int) {
        versionCode = vCode.toLong()
    }

    fun onBack() {
        viewModelScope.launch {
            _action.emit(
                AssignLocationState.Back
            )
        }
    }

    fun sendFleetsFromPerimeter() {
        viewModelScope.launch {
            if (selectedLocation.value != null) {
                val assignFleetModel = AssignFleetModel(
                    selectedLocation.value!!.id,
                    withPassenger = selectedLocation.value!!.isWithPassenger
                )
                checkIsSelectedFleetIsNotEmpty(assignFleetModel)
            } else {
                _action.emit(AssignLocationState.LocationIsNoSelected)
            }
        }
    }

    private suspend fun checkIsSelectedFleetIsNotEmpty(assignFleetModel: AssignFleetModel) {
        val list = getSelectedCar()
        if (list.isEmpty()) {
            _action.emit(AssignLocationState.SelectedCarIsEmpty)
        } else {
            assignFleetModel.carsAssignment = list
            selectAssignOrRitase(assignFleetModel)
        }
    }

    private fun selectAssignOrRitase(assignFleetModel: AssignFleetModel) {
        when (isPerimeter.value) {
            true -> {
                assignSendFleetTerminal(assignFleetModel)
            }
            else -> ritaseSendFleetTerminal(assignFleetModel)
        }
    }

    private fun assignSendFleetTerminal(assignFleetModel: AssignFleetModel) {
        viewModelScope.launch {
            _action.emit(AssignLocationState.SendFleetOnProgress)
            assignFleetTerminal.invoke(
                assignFleetModel
            ).flowOn(Dispatchers.Main).catch { cause ->
                _action.emit(AssignLocationState.OnError(cause))
            }.collect {
                when (it) {
                    is AssignFleetTerminalAirportState.Success -> {
                        _action.emit(
                            AssignLocationState.SendCarSuccess(
                                assignFleetModel.carsAssignment.size,
                                getMessage(), selectedLocation.value ?: AssignLocationModel()
                            )
                        )
                    }
                }
            }
        }
    }


    private fun ritaseSendFleetTerminal(assignFleetModel: AssignFleetModel) {
        viewModelScope.launch() {
            _action.emit(AssignLocationState.SendFleetOnProgress)
            ritaseFleetTerminal.invoke(
                assignFleetModel
            ).flowOn(Dispatchers.Main).catch { cause ->
                _action.emit(AssignLocationState.OnError(cause))
            }.collect {
                when (it) {
                    is RitaseFleetTerminalAirportState.Success -> {
                        _action.emit(
                            AssignLocationState.SendCarFromAirport(
                                assignFleetModel.carsAssignment.size,
                                getMessage(),
                                assignFleetModel.withPassenger,
                                assignFleetModel.isArrived
                            )
                        )
                    }
                }
            }
        }
    }

    fun getMessage(): Any {
        return if (selectedCarMap.size > 1) {
            selectedCarMap.size
        } else {
            selectedCarMap[0].fleetNumber
        }
    }

    private fun getSelectedCar(): List<FleetItemDepartModel> {
        return if (selectedCarMap.isNotEmpty()) {
            val result: MutableList<FleetItemDepartModel> = ArrayList()
            selectedCarMap.forEach {
                result.add(
                    FleetItemDepartModel(
                        taxiNo = it.fleetNumber,
                        createdAt = it.dateAfterConvert,
                        fleetId = it.stockId,
                        isSelected = it.isSelected,
                        status = it.status,
                        isTu = it.isTU,
                        sequence = it.sequence
                    )
                )
            }
            result
        } else {
            ArrayList()
        }
    }

    fun updateSelectedLocation(assignLocationModel: AssignLocationModel) {
        selectedLocation.value = assignLocationModel
    }

    fun updateLocationToAssign(assignLocationModel: AssignLocationModel) {
        selectedLocation.value = assignLocationModel
    }

    fun getAssignLocation() {
        viewModelScope.launch() {
            _action.emit(AssignLocationState.GetListProsess)
            getSubLocationAssigment.invoke(
                locationId = UserUtils.getLocationId(),
                showWingsChild = showWingsChild,
                versionCode = versionCode
            ).flowOn(Dispatchers.Main).catch { cause ->
                _action.emit(AssignLocationState.OnError(cause))
            }.collect {
                when (it) {
                    is GetSubLocationAirportState.Success -> {
                        val list: MutableList<AssignLocationModel> = ArrayList()
                        val temp = it.result
                        temp.countSubLocationItem.forEach { item ->
                            val assign = AssignLocationModel(
                                item.subLocationId,
                                item.subLocationName,
                                item.count,
                                isWithPassenger = item.withPassenger,
                                isNonTerminal = AssignLocationModel.isNonTerminal(item.subLocationName)
                            )
                            list.add(assign)
                        }
                        _action.emit(AssignLocationState.GetListSuccess(list))
                    }
                }
            }
        }
    }

}