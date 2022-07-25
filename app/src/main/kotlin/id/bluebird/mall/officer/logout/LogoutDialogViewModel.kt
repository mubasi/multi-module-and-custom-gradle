package id.bluebird.mall.officer.logout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.mall.core.utils.hawk.UserUtils
import id.bluebird.mall.domain.user.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class LogoutDialogViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _logoutDialogState: MutableSharedFlow<LogoutDialogState> =
        MutableSharedFlow()
    val logoutDialogState = _logoutDialogState.asSharedFlow()

    fun cancelDialog() {
        viewModelScope.launch {
            _logoutDialogState.emit(LogoutDialogState.CancelDialog)
        }
    }

    fun prosesDialog() {
        viewModelScope.launch {
//            _logoutDialogState.emit(LogoutDialogState.ProsesDialog)
            userRepository.forceLogout(UserUtils.getUUID())
                .catch { cause ->
                    _logoutDialogState.emit(LogoutDialogState.Err(cause))
                }
                .collect {
                    _logoutDialogState.emit(LogoutDialogState.ProsesDialog)
            }
        }
    }

}