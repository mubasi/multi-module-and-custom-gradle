package id.bluebird.mall.officer.logout


sealed class LogoutDialogState {
    object CancelDialog : LogoutDialogState()
    object ProsesDialog : LogoutDialogState()
    data class MessageError(val message: String) : LogoutDialogState()
    data class Err(val err: Throwable) : LogoutDialogState()
}