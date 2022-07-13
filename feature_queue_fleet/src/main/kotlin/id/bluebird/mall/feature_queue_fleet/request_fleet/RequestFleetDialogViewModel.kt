package id.bluebird.mall.feature_queue_fleet.request_fleet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.mall.domain_fleet.RequestState
import id.bluebird.mall.domain_fleet.domain.cases.RequestFleet
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
    val counter: MutableLiveData<Int> = MutableLiveData(1)
    private var _subLocationId = -1L

    fun initSubLocationId(subLocationId: Long) {
        _subLocationId = subLocationId
    }

    fun cancelFleetDialog() {
        viewModelScope.launch {
            _requestFleetDialogState.emit(RequestFleetDialogState.CancelDialog)
        }
    }

    fun addCounter() {
        var temp = counter.value ?: MINIMUM_COUNTER_VALUE
        temp++
        counter.value = temp
    }

    fun minusCounter() {
        var temp = counter.value ?: MINIMUM_COUNTER_VALUE
        if (temp > MINIMUM_COUNTER_VALUE) {
            temp--
            counter.value = temp
        }
    }

    fun requestFleet() {
        viewModelScope.launch {
            requestFleet.invoke(counter.value?.toLong() ?: -1L, _subLocationId)
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
}