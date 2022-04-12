package id.bluebird.mall.officer.common

import id.bluebird.mall.officer.ui.home.model.QueueCache

sealed class GeneralError : LoginState, HomeState {
    data class UnAuthorize(val message: String) : GeneralError()
    data class NotFound(val message: String) : GeneralError()
    data class NullPointerException(val message: String) : GeneralError()
    data class Unknown(val message: String) : GeneralError()
}

sealed class CommonState : LoginState, HomeState {
    object Progress : CommonState()
    object Idle : CommonState()
    object ConnectionNotFound : CommonState()
    data class Error(val error: Throwable) : CommonState()
}

sealed interface LoginState {
    object Phone : LoginState
    object Success : LoginState
    object LoginIgnored : LoginState
    object PasswordIsEmpty : LoginState
    object UsernameIsEmpty : LoginState
}

sealed interface HomeState {
    object Logout : HomeState
    object LogoutSuccess : HomeState
    object DummyIndicator : HomeState
    object CurrentQueueIsEmpty : HomeState
    object OnSync : HomeState
    object ParamSearchQueueEmpty : HomeState
    object ParamSearchQueueLessThanTwo : HomeState
    object ShowSearchResult : HomeState
    object SearchResultIsEmpty : HomeState
    data class SuccessRitase(val queueNumber: String) : HomeState
    data class SuccessSkiped(val queueNumber: String) : HomeState
    data class SuccessRestored(val queueNumber: String) : HomeState
}

sealed interface HomeDialogState {
    object Idle : HomeDialogState
    data class SkipCurrentQueue(val item: QueueCache) : HomeDialogState
    data class RestoreQueue(val item: QueueCache) : HomeDialogState
    data class SuccessCurrentQueue(val queueNumber: String) : HomeDialogState
}

sealed class CasesResult<out T : Any> {
    data class OnSuccess<out T : Any>(val result: T) : CasesResult<T>()
    data class OnError(val generalError: GeneralError) :
        CasesResult<Nothing>()
}
