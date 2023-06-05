package id.bluebird.vsm.feature.airport_fleet.dialog_request_stock

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.domain.airport_assignment.RequestTaxiDepartState
import id.bluebird.vsm.domain.airport_assignment.domain.cases.RequestTaxiDepart
import id.bluebird.vsm.domain.fleet.RequestState
import id.bluebird.vsm.domain.fleet.domain.cases.RequestFleet
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DialogButtomRequestStockViewModel(
    private val requestTaxi: RequestTaxiDepart
) : ViewModel()  {
    companion object {
        const val MINIMUM_REQUEST_TAXI = "1"
        const val MINIMUM_COUNTER_VALUE = 1
        const val INVALID_SUB_LOCATION = "sublocation tidak sesuai"
        const val INVALID_COUNTER = "Permintaan tidak boleh kosong"
    }

    private val _action: MutableSharedFlow<DialogRequestStockState> =
        MutableSharedFlow()
    val action = _action.asSharedFlow()
    val requestTaxiCounter: MutableLiveData<String> = MutableLiveData(MINIMUM_REQUEST_TAXI)
    private var _subLocationId : Long = -1L
    private var _requestToId : Long = -1L

    @VisibleForTesting
    fun getSubLocationId() : Long {
        return _subLocationId
    }

    @VisibleForTesting
    fun getRequestToId() : Long {
        return _requestToId
    }

    @VisibleForTesting
    fun setRequestTaxiCounter(value : String) {
        requestTaxiCounter.value = value
    }

    @VisibleForTesting
    fun getValCounter() : Int {
        return getValueCounter()
    }

    fun initSubLocationId(subLocationId : Long, requestToId : Long) {
        _subLocationId = subLocationId
        requestTaxiCounter.value = MINIMUM_REQUEST_TAXI
        _requestToId = requestToId
    }

    fun cancelFleetDialog() {
        viewModelScope.launch {
            _action.emit(DialogRequestStockState.CancleDialog)
        }
    }

    fun focusableEnable(){
        viewModelScope.launch {
            _action.emit(DialogRequestStockState.FocusState(true))
        }
    }

    fun subtractRequestFleet(){
        viewModelScope.launch {
            checkMinimumCounter()
            _action.emit(DialogRequestStockState.FocusState(false))
        }
    }

    private fun checkMinimumCounter() {
        if (getValueCounter() > MINIMUM_COUNTER_VALUE) {
            requestTaxiCounter.value = (getValueCounter() - 1).toString()
        }
    }

    fun addRequestFleet(){
        requestTaxiCounter.value = (getValueCounter() + 1).toString()
        viewModelScope.launch {
            _action.emit(DialogRequestStockState.FocusState(false))
        }
    }

    fun sendFleetRequest(){
        viewModelScope.launch() {
            _action.emit(DialogRequestStockState.SendRequestTaxiOnProgress)
            requestTaxi.invoke(
                _subLocationId,
                _requestToId,
                getValueCounter().toLong(),
            )
                .catch { cause ->
                    _action.emit(DialogRequestStockState.Err(cause))
                }.collect {
                    when (it) {
                        RequestTaxiDepartState.CountInvalid -> {
                            _action.emit(
                                DialogRequestStockState.MessageError(INVALID_COUNTER)
                            )
                        }
                        RequestTaxiDepartState.SubLocationInvalid -> {
                            _action.emit(
                                DialogRequestStockState.MessageError(INVALID_SUB_LOCATION)
                            )
                        }
                        is RequestTaxiDepartState.Success -> {
                            _action.emit(DialogRequestStockState.RequestSuccess(it.result.requestCount))
                        }
                    }
                    cancel()
                }
        }
    }

    private fun getValueCounter(): Int {
        return (requestTaxiCounter.value.orEmpty().toIntOrNull() ?: -1)
    }

}