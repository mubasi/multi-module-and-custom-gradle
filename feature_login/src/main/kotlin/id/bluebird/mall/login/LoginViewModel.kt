package id.bluebird.mall.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.mall.core.CommonState
import id.bluebird.mall.core.LoginState
import id.bluebird.mall.core.utils.hawk.AuthUtils.putAccessToken
import id.bluebird.mall.domain.user.domain.intercator.Login
import kotlinx.coroutines.delay
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
    private var ignoreStaticClass = false

    // akan dihapus dimasa yg akan datang
    private var ignoreLogin = false

    init {
        version.value = BuildConfig.VERSION_NAME
    }

    fun login() {
        viewModelScope.launch {
            loginState.postValue(CommonState.Progress)
            if (ignoreLogin.not()) {
                loginCase.invoke(username.value, password.value)
                    .catch { e ->
                        loginState.postValue(CommonState.Error(e))
                    }.collectLatest {
//                        when (val res = it) {
//                            is CasesResult.OnSuccess -> {
//                                if (ignoreStaticClass.not()) {
//                                    res.result.putAccessToken()
//                                }
//                                loginState.postValue(LoginState.Success)
//                            }
//                            is CasesResult.OnError -> {
//                                loginState.postValue(res.generalError)
//                            }
//                        }
                    }
            } else {
                delay(1000)
                loginState.postValue(LoginState.Success)
                "".putAccessToken()
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