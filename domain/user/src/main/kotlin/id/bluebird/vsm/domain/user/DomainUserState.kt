package id.bluebird.vsm.domain.user

import id.bluebird.vsm.domain.user.model.CreateUserResult
import id.bluebird.vsm.domain.user.model.SearchUserResult

sealed class UserDomainState<out T : Any> {
    data class Success<out T : Any>(val value: T) : UserDomainState<T>()
}

sealed class GetUserByIdState {
    data class Success(val result: CreateUserResult) : GetUserByIdState()
}

sealed class SearchUserState{
    data class Success(val searchUserResult: SearchUserResult) : SearchUserState()
}

sealed interface UserErr {
    object UsernameIsEmpty : UserErr, UserDomainState<Nothing>()
    object NameIsEmpty : UserErr, UserDomainState<Nothing>()
    object PasswordIsEmpty : UserErr, UserDomainState<Nothing>()
    object NewPasswordIsEmpty : UserErr, UserDomainState<Nothing>()
    object RoleIsNotSelected : UserErr, UserDomainState<Nothing>()
}

