package id.bluebird.vsm.feature.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.domain.user.domain.intercator.Login
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginCase: Login
) :
    ViewModel() {

    val loginState: MutableLiveData<LoginState> = MutableLiveData()
    val visibilityPassword: MutableLiveData<Boolean> = MutableLiveData(false)
    val password: MutableLiveData<String> = MutableLiveData()
    val username: MutableLiveData<String> = MutableLiveData()
    val version: MutableLiveData<String> = MutableLiveData()

    companion object {
        private const val NOT_FOUND = "Wrong username and password"
    }

    init {
        version.value = BuildConfig.VERSION_NAME
    }

    fun login() {
        viewModelScope.launch {
            loginState.postValue(LoginState.LoginProgress)
            loginCase.invoke(username.value, password.value)
                .catch { e ->
                    loginState.postValue(LoginState.Error(getError(e.message)))
                }.collectLatest {
                    loginState.postValue(LoginState.Success)
                }
        }
    }

    fun callPhone() {
        loginState.value = LoginState.Phone
    }

    private fun getError(message: String?): ErrorType {
        return when (message?.lowercase()) {
            NOT_FOUND.lowercase() -> ErrorType.UserNotFound
            else -> ErrorType.Unknown
        }
    }
}