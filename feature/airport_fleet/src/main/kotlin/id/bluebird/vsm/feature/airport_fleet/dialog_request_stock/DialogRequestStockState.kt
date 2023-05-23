package id.bluebird.vsm.feature.airport_fleet.dialog_request_stock

sealed class DialogRequestStockState {

    object SendRequestTaxiOnProgress : DialogRequestStockState()
    object CancleDialog : DialogRequestStockState()
    data class FocusState(val isFocus: Boolean) : DialogRequestStockState()
    data class MessageError(val message: String) : DialogRequestStockState()
    data class Err(val err: Throwable) : DialogRequestStockState()
    data class RequestSuccess(val count: Long) : DialogRequestStockState()

}