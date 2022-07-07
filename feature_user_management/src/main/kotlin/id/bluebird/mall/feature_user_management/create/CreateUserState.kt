package id.bluebird.mall.feature_user_management.create

import id.bluebird.mall.domain.user.UserErr


sealed class CreateUserState {
    object OnBack : CreateUserState()
    object OnGetDataProcess : CreateUserState()
    object GetUserStateSuccess : CreateUserState()
    object OnSaveProgress : CreateUserState()
    object GetSubLocation : CreateUserState()
    data class InvalidField(val err: UserErr) : CreateUserState()
    object GetInformationSuccess : CreateUserState()
    data class OnSuccess(val name: String, val isCreateUser: Boolean) : CreateUserState()
    data class OnError(val err: Throwable) : CreateUserState()
    data class GetInformationOnError(val err: Throwable) : CreateUserState()
}