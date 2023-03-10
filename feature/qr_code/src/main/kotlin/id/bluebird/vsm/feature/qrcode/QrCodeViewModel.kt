package id.bluebird.vsm.feature.qrcode

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.domain.location.GetLocationQrCodeState
import id.bluebird.vsm.domain.location.domain.interactor.GetSubLocationQrCode
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class QrCodeViewModel(
    private val getSubLocationQrCode: GetSubLocationQrCode
) : ViewModel() {
    companion object {
        const val EMPTY_STRING = ""
    }

    private val _qrCodeState: MutableSharedFlow<QrCodeState> = MutableSharedFlow()
    val qrCodeState = _qrCodeState.asSharedFlow()

    var _locationId: Long = -1
    var _subLocationId: Long = -1
    var titleLocation: MutableLiveData<String> = MutableLiveData(EMPTY_STRING)
    var qrCodeDriver: String? = null
    var qrCodeWeb: String? = null

    @VisibleForTesting
    fun setQrCode(position: Int, result: String) {
        if (position == 0) {
            qrCodeDriver = result
        } else {
            qrCodeWeb = result
        }
    }

    fun init(locationId: Long, subLocationId: Long, title: String) {
        _locationId = locationId
        _subLocationId = subLocationId
        titleLocation.postValue(title)
    }

    fun changeQrCode(position: Int) {
        if (qrCodeDriver.isNullOrEmpty() || qrCodeWeb.isNullOrEmpty()) {
            loadQrCode(position)
            return
        } else {
            setQrCode(position)
        }
    }

    private fun loadQrCode(position: Int) {
        viewModelScope.launch {
            _qrCodeState.emit(QrCodeState.Progress)
            getSubLocationQrCode.invoke(
                _subLocationId
            ).catch { cause ->
                _qrCodeState.emit(
                    QrCodeState.OnError(cause)
                )
            }.collect {
                when (it) {
                    is GetLocationQrCodeState.Success -> {
                        qrCodeDriver = it.result.daQrCode
                        qrCodeWeb = it.result.queuePassengerQrCode
                        setQrCode(position)
                    }
                }
            }
        }
    }

    fun setQrCode(position: Int) {
        viewModelScope.launch {
            _qrCodeState.emit(
                QrCodeState.SuccessLoad(
                    setValQrCode(position)
                )
            )
        }
    }

    private fun setValQrCode(position: Int): String {
        return if (position == 0) {
            qrCodeDriver ?: EMPTY_STRING
        } else {
            qrCodeWeb ?: EMPTY_STRING
        }
    }


}