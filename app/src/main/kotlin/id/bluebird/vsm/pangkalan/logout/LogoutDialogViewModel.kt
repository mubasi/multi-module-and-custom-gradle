package id.bluebird.vsm.pangkalan.logout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LogoutDialogViewModel() : ViewModel() {

    private val _logoutDialogState: MutableSharedFlow<LogoutDialogState> =
        MutableSharedFlow()
    val logoutDialogState = _logoutDialogState.asSharedFlow()

    fun cancelDialog() {
        viewModelScope.launch {
            _logoutDialogState.emit(LogoutDialogState.CancelDialog)
        }
    }

    fun prosesDialog() {
//        viewModelScope.launch {
//            userRepository.forceLogout(UserUtils.getUUID())
//                .catch { cause ->
//                    _logoutDialogState.emit(LogoutDialogState.Err(cause))
//                }
//                .collect {
//                    _logoutDialogState.emit(LogoutDialogState.ProsesDialog)
//            }
//        }
    }

}