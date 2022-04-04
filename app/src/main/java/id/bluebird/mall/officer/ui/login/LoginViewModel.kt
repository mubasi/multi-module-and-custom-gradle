package id.bluebird.mall.officer.ui.login

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.mall.officer.BuildConfig
import id.bluebird.mall.officer.common.CasesResult
import id.bluebird.mall.officer.common.CommonState
import id.bluebird.mall.officer.common.LoginState
import id.bluebird.mall.officer.common.uses_case.user.LoginCase
import id.bluebird.mall.officer.utils.AuthUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginCase: LoginCase
) :
    ViewModel() {

    val loginState: MutableLiveData<LoginState> = MutableLiveData()
    val visibilityPassword: MutableLiveData<Boolean> = MutableLiveData(false)
    val password: MutableLiveData<String> = MutableLiveData()
    val username: MutableLiveData<String> = MutableLiveData()
    val version: MutableLiveData<String> = MutableLiveData()
    private var ignoreStaticClass = false

    // akan dihapus dimasa yg akan datang
    private var ignoreLogin = false

    init {
        version.value = BuildConfig.VERSION_NAME
    }

    @VisibleForTesting
    fun ignoreStatic() {
        ignoreStaticClass = true
    }

    fun login() {
        viewModelScope.launch {
            loginState.postValue(CommonState.Progress)
            if (ignoreLogin.not()) {
                loginCase.invoke(username.value, password.value)
                    .catch { e ->
                        loginState.postValue(CommonState.Error(e))
                    }.collectLatest {
                        when (val res = it) {
                            is CasesResult.OnSuccess -> {
                                if (ignoreStaticClass.not()) {
                                    AuthUtils.putAccessToken(res.result)
                                }
                                loginState.postValue(LoginState.Success)
                            }
                            is CasesResult.OnError -> {
                                loginState.postValue(res.generalError)
                            }
                        }
                    }
            } else {
                delay(1000)
                loginState.postValue(LoginState.Success)
                AuthUtils.putAccessToken("")
            }
        }
    }

    fun callPhone() {
        loginState.value = LoginState.Phone
    }

    // akan dihapus dimasa yg akan datang
    fun ignoreLoginLogic() {
        ignoreLogin = true
        loginState.value = LoginState.LoginIgnored
    }
}