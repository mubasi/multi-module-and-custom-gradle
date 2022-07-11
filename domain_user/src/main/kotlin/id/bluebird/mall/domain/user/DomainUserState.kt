package id.bluebird.mall.domain.user

import id.bluebird.mall.domain.user.model.CreateUserResult

sealed class UserDomainState<out T : Any> {
    data class Success<out T : Any>(val value: T) : UserDomainState<T>()
}

sealed class GetUserByIdState {
    data class Success(val result: CreateUserResult) : GetUserByIdState()
    object UserIdIsWrong : GetUserByIdState()
}

sealed interface UserErr {
    object UserIdIsLess : UserErr, UserDomainState<Nothing>()
    object UsernameIsEmpty : UserErr, UserDomainState<Nothing>()
    object NameIsEmpty : UserErr, UserDomainState<Nothing>()
    object PasswordIsEmpty : UserErr, UserDomainState<Nothing>()
    object NewPasswordIsEmpty : UserErr, UserDomainState<Nothing>()
    object RoleIsNotSelected : UserErr, UserDomainState<Nothing>()
}

