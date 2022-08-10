package id.bluebird.mall.feature_monitoring.edit_buffer

sealed class EditBufferState {
    object ClosingDialog: EditBufferState()
    object SuccessSave: EditBufferState()
    object OnProgressSave: EditBufferState()
    object FailedSave: EditBufferState()
    data class FocusState(val isFocus: Boolean): EditBufferState()
}
