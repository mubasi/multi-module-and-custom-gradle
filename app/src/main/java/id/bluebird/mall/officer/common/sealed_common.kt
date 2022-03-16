package id.bluebird.mall.officer.common

sealed interface GeneralError

sealed interface CommonState {
    object Progress : CommonState
    object Idle : CommonState
    data class Error(val error: Throwable) : CommonState
}

sealed class LoginState : CommonState {
    object Phone : LoginState()
    object Success : LoginState()
}

sealed class HomeState : CommonState {
    object Logout : HomeState()
    object DummyIndicator    : HomeState()
    object OnSync : HomeState()
}