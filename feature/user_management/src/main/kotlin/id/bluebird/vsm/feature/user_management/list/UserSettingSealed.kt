package id.bluebird.vsm.feature.user_management.list

sealed class UserSettingSealed {
    object Idle : UserSettingSealed()
    object CreateUser : UserSettingSealed()
    object OnGetUserListProgress : UserSettingSealed()
    data class CreateUserSuccess(val name: String) : UserSettingSealed()
    data class EditUserSuccess(val name: String) : UserSettingSealed()
    data class GetUsers(val list: List<UserSettingCache>) : UserSettingSealed()
    data class DeleteSuccess(val name: String) : UserSettingSealed()
    data class ForceSuccess(val name: String) : UserSettingSealed()
    data class Delete(val userSettingCache: UserSettingCache) : UserSettingSealed()
    data class EditUser(val userSettingCache: UserSettingCache) : UserSettingSealed()
    data class ForceLogout(val userSettingCache: UserSettingCache) : UserSettingSealed()
    data class GetUserOnError(val err: Throwable) : UserSettingSealed()
}