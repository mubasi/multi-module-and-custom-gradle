package id.bluebird.mall.officer.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.mall.officer.common.CommonState
import id.bluebird.mall.officer.common.LoginState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel(private val dispatcher: CoroutineDispatcher = Dispatchers.Default) :
    ViewModel() {

    val loginState: MutableLiveData<CommonState> = MutableLiveData()
    val visibilityPassword: MutableLiveData<Boolean> = MutableLiveData(false)
    val password: MutableLiveData<String> = MutableLiveData()
    val username: MutableLiveData<String> = MutableLiveData()

    fun changePasswordVisibility() {
        visibilityPassword.value =
            visibilityPassword.value == null || visibilityPassword.value == false
    }

    fun login() {
        viewModelScope.launch(dispatcher) {
            loginState.postValue(CommonState.Progress)
            delay(2000)
            loginState.postValue(CommonState.Error(Throwable("User tidak ditemukan")))
        }
    }

    fun callPhone(){
        loginState.value = LoginState.Phone
    }
}