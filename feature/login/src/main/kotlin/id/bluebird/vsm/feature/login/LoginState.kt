package id.bluebird.vsm.feature.login

sealed interface LoginState {
    object Phone : LoginState
    object Success : LoginState
    object LoginProgress : LoginState
    object PasswordIsEmpty : LoginState
    object UsernameIsEmpty : LoginState
    object Idle : LoginState
    data class Error(val errorType: ErrorType) : LoginState
}

sealed class ErrorType {
    object UserNotFound : ErrorType()
    object Unknown : ErrorType()
}