package id.bluebird.mall.domain.user

sealed class UserDomainState<out T : Any> {
    data class Success<out T : Any>(val value: T) : UserDomainState<T>()
}

sealed interface UserErr {
    object UserIdIsLess : UserErr, UserDomainState<Nothing>()
    object UsernameIsEmpty : UserErr, UserDomainState<Nothing>()
    object NameIsEmpty : UserErr, UserDomainState<Nothing>()
    object PasswordIsEmpty : UserErr, UserDomainState<Nothing>()
    object NewPasswordIsEmpty : UserErr, UserDomainState<Nothing>()
    object RoleIsNotSelected : UserErr, UserDomainState<Nothing>()
}

