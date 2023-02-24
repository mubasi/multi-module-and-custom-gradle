package id.bluebird.vsm.feature.home.dialog_record_ritase

sealed class DialogRecordRitaseState {
    object Idle : DialogRecordRitaseState()
    object ProgressDialog : DialogRecordRitaseState()
    object CancelDialog : DialogRecordRitaseState()
    data class OnError(val err : Throwable, val fleetNumber: String) : DialogRecordRitaseState()
    object SelectFleet : DialogRecordRitaseState()
    object FleetEmpty : DialogRecordRitaseState()
    data class SuccessRitase(
        val fleetNumber : String,
        val queueNumber : String,
    ) : DialogRecordRitaseState()
}