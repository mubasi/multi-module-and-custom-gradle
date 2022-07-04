package id.bluebird.mall.feature_user_management.list

sealed class UserSettingSealed {
    object CreateUser : UserSettingSealed()
    object OnGetUserListProgress : UserSettingSealed()
    data class GetUsers(val list: List<UserSettingCache>) : UserSettingSealed()
    data class DeleteSuccess(val userSettingCache: UserSettingCache) : UserSettingSealed()
    data class ForceSuccess(val userSettingCache: UserSettingCache) : UserSettingSealed()
    data class Delete(val userSettingCache: UserSettingCache) : UserSettingSealed()
    data class EditUser(val userSettingCache: UserSettingCache) : UserSettingSealed()
    data class ForceLogout(val userSettingCache: UserSettingCache) : UserSettingSealed()
    data class GetUserOnError(val err: Throwable) : UserSettingSealed()
}