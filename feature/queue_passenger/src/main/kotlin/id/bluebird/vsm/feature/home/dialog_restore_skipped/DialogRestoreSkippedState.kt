package id.bluebird.vsm.feature.home.dialog_restore_skipped

sealed class DialogRestoreSkippedState {
    object ProsesRestoreQueueSkipped : DialogRestoreSkippedState()
    object SuccessRestoreQueueSkipped : DialogRestoreSkippedState()
    object CancelRestoreQueueSkipped : DialogRestoreSkippedState()
    data class FailedRestoreQueueSkipped(val message: String) : DialogRestoreSkippedState()
}