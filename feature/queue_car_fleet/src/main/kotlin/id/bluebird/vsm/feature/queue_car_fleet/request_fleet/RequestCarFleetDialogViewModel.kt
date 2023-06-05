package id.bluebird.vsm.feature.queue_car_fleet.request_fleet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.fleet.RequestState
import id.bluebird.vsm.domain.fleet.domain.cases.RequestFleet
import id.bluebird.vsm.feature.select_location.LocationNavigationTemporary
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class RequestCarFleetDialogViewModel(private val requestFleet: RequestFleet, val dispatcher: CoroutineDispatcher = Dispatchers.Default) : ViewModel() {
    companion object {

        const val MINIMUM_COUNTER_VALUE = 1
        const val INVALID_SUB_LOCATION = "sublocation tidak sesuai"
        const val INVALID_COUNTER = "counter harus lebih besar dari 1"
    }

    private val _requestCarFleetDialogState: MutableSharedFlow<RequestCarFleetDialogState> =
        MutableSharedFlow()
    val requestFleetDialogState = _requestCarFleetDialogState.asSharedFlow()
    val counter: MutableLiveData<String> = MutableLiveData("1")
    private var _subLocationId = -1L

    fun initSubLocationId(subLocationId: Long) {
        _subLocationId = subLocationId
        counter.value = "1"
    }

    fun cancelFleetDialog() {
        viewModelScope.launch {
            _requestCarFleetDialogState.emit(RequestCarFleetDialogState.CancelDialogCar)
        }
    }

    fun focusableEnable() {
        viewModelScope.launch {
            _requestCarFleetDialogState.emit(RequestCarFleetDialogState.FocusStateCar(true))
        }
    }

    fun addCounter() {
        counter.value = (getValueCounter() + 1).toString()
        viewModelScope.launch {
            _requestCarFleetDialogState.emit(RequestCarFleetDialogState.FocusStateCar(false))
        }
    }

    fun minusCounter() {
        if (getValueCounter() > MINIMUM_COUNTER_VALUE) {
            counter.value = (getValueCounter() - 1).toString()
        }
        viewModelScope.launch {
            _requestCarFleetDialogState.emit(RequestCarFleetDialogState.FocusStateCar(false))
        }
    }

    fun requestFleet() {
        viewModelScope.launch {
            _requestCarFleetDialogState.emit(
                RequestCarFleetDialogState.ProcessRequestCar
            )
            requestFleet.invoke(
                count = getValueCounter().toLong(),
                subLocationId = _subLocationId,
                locationId = LocationNavigationTemporary.getLocationNav()?.locationId
                    ?: UserUtils.getLocationId()
            )
                .catch { cause ->
                    _requestCarFleetDialogState.emit(
                        RequestCarFleetDialogState.Err(cause)
                    )
                }
                .collect {
                    when (it) {
                        RequestState.CountInvalid -> {
                            _requestCarFleetDialogState.emit(
                                RequestCarFleetDialogState.MessageError(INVALID_COUNTER)
                            )
                        }
                        RequestState.SubLocationInvalid -> {
                            _requestCarFleetDialogState.emit(
                                RequestCarFleetDialogState.MessageError(INVALID_SUB_LOCATION)
                            )
                        }
                        is RequestState.Success -> {
                            _requestCarFleetDialogState.emit(
                                RequestCarFleetDialogState.RequestCarSuccess(it.count)
                            )
                        }
                    }
                    cancel()
                }
        }
    }

    private fun getValueCounter(): Int {
        return (counter.value.orEmpty().toIntOrNull() ?: 0)
    }
}