package id.bluebird.vsm.feature.airport_fleet.dialog_request_stock

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.domain.fleet.domain.cases.RequestFleet
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DialogButtomRequestStockViewModel(
    private val requestTaxi: RequestFleet
) : ViewModel()  {
    companion object {
        const val DEFAULT_REQUEST = "1"
        const val MINIMUM_REQUEST_TAXI = "1"
        const val MINIMUM_COUNTER_VALUE = 1
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
        requestTaxiCounter.value = DEFAULT_REQUEST
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
                getValueCounter().toLong(),
                _subLocationId,
                _requestToId
            )
                .catch { cause ->
                    _action.emit(DialogRequestStockState.Err(cause))
                }.collect {
                    when (it) {
//                        is RequestTaxiState.Success -> {
//                            _action.emit(DialogRequestStockState.RequestSuccess(it.requestTaxiResult.requestCount))
//                        }
                    }
                }
        }
    }

    private fun getValueCounter(): Int {
        return (requestTaxiCounter.value.orEmpty().toIntOrNull() ?: -1)
    }

}