package id.bluebird.mall.feature_user_management.list

import android.text.Editable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.mall.core.utils.hawk.UserUtils
import id.bluebird.mall.domain.user.domain.intercator.DeleteUser
import id.bluebird.mall.domain.user.domain.intercator.ForceLogout
import id.bluebird.mall.domain.user.domain.intercator.SearchUser
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class UserManagementViewModel(
    private val deleteUser: DeleteUser,
    private val searchUser: SearchUser,
    private val forceLogout: ForceLogout
) : ViewModel() {
    val counter: MutableLiveData<Int> = MutableLiveData()
    val userSettingSealed: MutableLiveData<UserSettingSealed> = MutableLiveData()
    val privilege: MutableLiveData<String> = MutableLiveData()
    private val userSettings: MutableList<UserSettingCache> = ArrayList()
    private var searchJob: Job? = null
    private var loginUserId: Long? = -1

    fun init() {
        val temp = UserUtils.getPrivilege()
        loginUserId = UserUtils.getUserId()
        val s = if (temp == UserUtils.SVP) {
            UserUtils.SVP
        } else {
            UserUtils.ADMIN
        }
        privilege.value = s
    }


    fun onSearch(editable: Editable?) {
        searchJob?.let {
            if (it.isActive) {
                it.cancel()
            }
        }
        searchJob = viewModelScope.launch {
            userSettingSealed.postValue(UserSettingSealed.OnGetUserListProgress)
            delay(1000)
            searchUser(editable.toString().trim())
        }
    }

    fun searchUser() {
        viewModelScope.launch {
            userSettingSealed.postValue(UserSettingSealed.OnGetUserListProgress)
            searchUser("")
        }
    }

    private suspend fun searchUser(param: String?) {
        searchUser.invoke(param)
            .catch { cause: Throwable ->
                userSettingSealed.postValue(UserSettingSealed.GetUserOnError(cause))
            }
            .collect {
                userSettings.clear()
                it.forEach { userSearch ->
                    if (userSearch.id != UserUtils.getUserId()) {
                        userSearch.apply {
                            userSettings.add(
                                UserSettingCache(
                                    id = id,
                                    userName = username,
                                    uuid = uuid,
                                    status = status
                                )
                            )
                        }
                    }
                }
                userSettingSealed.postValue(UserSettingSealed.GetUsers(userSettings))
                counter.postValue(userSettings.size)
            }
    }

    fun delete(userSettingCache: UserSettingCache) {
        viewModelScope.launch {
            userSettingSealed.postValue(UserSettingSealed.OnGetUserListProgress)
            deleteUser.invoke(userSettingCache.uuid)
                .catch { cause: Throwable ->
                    userSettingSealed.postValue(UserSettingSealed.GetUserOnError(cause))
                }
                .collect {
                    userSettingSealed.postValue(UserSettingSealed.DeleteSuccess(userSettingCache))
                    delay(200)
                    updateUserList(userSettingCache, true)
                }
        }
    }

    fun forceLogout(userSettingCache: UserSettingCache) {
        viewModelScope.launch {
            userSettingSealed.postValue(UserSettingSealed.OnGetUserListProgress)
            forceLogout.invoke(userSettingCache.uuid)
                .catch { cause: Throwable ->
                    userSettingSealed.postValue(UserSettingSealed.GetUserOnError(cause))
                }
                .collect {
                    userSettingSealed.postValue(UserSettingSealed.ForceSuccess(userSettingCache))
                    updateUserList(userSettingCache, false)
                }
        }
    }

    private fun updateUserList(userSettingCache: UserSettingCache, isRemove: Boolean) {
        for (i in 0 until userSettings.size) {
            val temp = userSettings[i]
            if (temp.id == userSettingCache.id) {
                if (isRemove) {
                    userSettings.removeAt(i)
                } else {
                    temp.status = false
                }
                userSettingSealed.postValue(UserSettingSealed.GetUsers(userSettings))
                counter.postValue(userSettings.size)
                break
            }
        }
    }

    fun result(name: String, isActionCreate: Boolean) {
        viewModelScope.launch {
            delay(300)
            if (isActionCreate) {
                userSettingSealed.postValue(UserSettingSealed.CreateUserSuccess(name))
            } else {
                userSettingSealed.postValue(UserSettingSealed.EditUserSuccess(name))
            }
        }
    }

    fun setIdle() {
        userSettingSealed.value = UserSettingSealed.Idle
    }

    fun onCreateUser() {
        userSettingSealed.value = UserSettingSealed.CreateUser
    }

    fun onEditUser(userSettingCache: UserSettingCache) {
        userSettingSealed.value = UserSettingSealed.EditUser(userSettingCache)
    }

    fun onDeleteUser(userSettingCache: UserSettingCache) {
        userSettingSealed.value = UserSettingSealed.Delete(userSettingCache)
    }

    fun onForceLogoutUser(userSettingCache: UserSettingCache) {
        userSettingSealed.value = UserSettingSealed.ForceLogout(userSettingCache)
    }
}