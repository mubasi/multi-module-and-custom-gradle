package id.bluebird.mall.officer.common

import id.bluebird.mall.officer.ui.home.QueueCache

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
    object DummyIndicator : HomeState()
    object OnSync : HomeState()
    object ParamSearchQueueEmpty : HomeState()
    object ParamSearchQueueLessThanTwo : HomeState()
    data class SuccessRitase(val queueNumber: String) : HomeState()
    data class SuccessSkiped(val queueNumber: String) : HomeState()
    data class SkipCurrentQueue(val item: QueueCache) : HomeState()
    data class SuccessCurrentQueue(val queueNumber: String) : HomeState()
}