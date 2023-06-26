package id.bluebird.vsm.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.core.utils.hawk.AuthUtils
import id.bluebird.vsm.core.utils.hawk.UserUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SplashViewModel(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private val _splashState: MutableSharedFlow<SplashState> = MutableSharedFlow()
    val splashState = _splashState.asSharedFlow()

    fun validateUser() {
        viewModelScope.launch {
            validateUserLogin()
        }
    }

    private suspend fun validateUserLogin() {
        delay(1000)
        if (AuthUtils.getAccessToken().isEmpty().not()) {
            validateUserType()
        } else {
            _splashState.emit(SplashState.Login)
        }
    }

    private suspend fun validateUserType() {
        with(UserUtils) {
            _splashState.emit(
                if (getIsUserAirport()) {
                    SplashState.LoginAsAirportUser
                } else {
                    SplashState.LoginAsOutletUser
                }
            )
        }
    }


//    fun checkNewVersion(codeVersion: Long? = null) {
//        viewModelScope.launch(coroutineDispatcher) {
//            validateForceUpdate.invoke(
//                key = id.bluebird.vsm.core.BuildConfig.SPLASH_KEY,
//                codeVersion = codeVersion
//            ).catch {
//                validateUserLogin()
//            }.collectLatest {
//                when (it) {
//                    is ValidateForceUpdateState.FoundNewVersion -> {
//                        _splashState.emit(
//                            SplashState.DoUpdateVersion(
//                                url = it.url,
//                                versionName = it.versionName
//                            )
//                        )
//                    }
//                    ValidateForceUpdateState.CodeVersionNotFound,
//                    ValidateForceUpdateState.NotFoundNewVersion -> {
//                        validateUserLogin()
//                    }
//                }
//            }
//        }
//    }
}