package id.bluebird.mall.home.dialog_delete_skipped

sealed class DialogDeleteSkippedState {
    object ProsesDeleteQueueSkipped : DialogDeleteSkippedState()
    object SuccessDeleteQueueSkipped : DialogDeleteSkippedState()
    object CancelDeleteQueueSkipped : DialogDeleteSkippedState()
    data class FailedDeleteQueueSkipped(val message: String) : DialogDeleteSkippedState()
}