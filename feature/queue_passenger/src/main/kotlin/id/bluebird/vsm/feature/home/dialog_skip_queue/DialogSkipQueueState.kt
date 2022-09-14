package id.bluebird.vsm.feature.home.dialog_skip_queue

sealed class DialogSkipQueueState {
    object CancleDialog: DialogSkipQueueState()
    object ProsessDialog: DialogSkipQueueState()
    object SuccessDialog: DialogSkipQueueState()
    data class FailedDialog(val message: String) : DialogSkipQueueState()
}