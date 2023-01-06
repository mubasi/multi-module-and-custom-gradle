package id.bluebird.vsm.feature.queue_fleet.request_fleet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.fleet.RequestState
import id.bluebird.vsm.domain.fleet.domain.cases.RequestFleet
import id.bluebird.vsm.feature.select_location.LocationNavigationTemporary
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class RequestFleetDialogViewModel(private val requestFleet: RequestFleet) : ViewModel() {
    companion object {

        const val MINIMUM_COUNTER_VALUE = 1
        const val INVALID_SUB_LOCATION = "sublocation tidak sesuai"
        const val INVALID_COUNTER = "counter harus lebih besar dari 1"
    }

    private val _requestFleetDialogState: MutableSharedFlow<RequestFleetDialogState> =
        MutableSharedFlow()
    val requestFleetDialogState = _requestFleetDialogState.asSharedFlow()
    val counter: MutableLiveData<String> = MutableLiveData("1")
    private var _subLocationId = -1L

    fun initSubLocationId(subLocationId: Long) {
        _subLocationId = subLocationId
        counter.value = "1"
    }

    fun cancelFleetDialog() {
        viewModelScope.launch {
            _requestFleetDialogState.emit(RequestFleetDialogState.CancelDialog)
        }
    }

    fun focusableEnable() {
        viewModelScope.launch {
            _requestFleetDialogState.emit(RequestFleetDialogState.FocusState(true))
        }
    }

    fun addCounter() {
        counter.value = (getValueCounter() + 1).toString()
        viewModelScope.launch {
            _requestFleetDialogState.emit(RequestFleetDialogState.FocusState(false))
        }
    }

    fun minusCounter() {
        if (getValueCounter() > MINIMUM_COUNTER_VALUE) {
            counter.value = (getValueCounter() - 1).toString()
        }
        viewModelScope.launch {
            _requestFleetDialogState.emit(RequestFleetDialogState.FocusState(false))
        }
    }

    fun requestFleet() {
        viewModelScope.launch {
            requestFleet.invoke(
                count = getValueCounter().toLong(),
                subLocationId = _subLocationId,
                locationId = LocationNavigationTemporary.getLocationNav()?.locationId
                    ?: UserUtils.getLocationId()
            )
                .catch { cause ->
                    _requestFleetDialogState.emit(
                        RequestFleetDialogState.Err(cause)
                    )
                }
                .collect {
                    when (it) {
                        RequestState.CountInvalid -> {
                            _requestFleetDialogState.emit(
                                RequestFleetDialogState.MessageError(INVALID_COUNTER)
                            )
                        }
                        RequestState.SubLocationInvalid -> {
                            _requestFleetDialogState.emit(
                                RequestFleetDialogState.MessageError(INVALID_SUB_LOCATION)
                            )
                        }
                        is RequestState.Success -> {
                            _requestFleetDialogState.emit(
                                RequestFleetDialogState.RequestSuccess(it.count)
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