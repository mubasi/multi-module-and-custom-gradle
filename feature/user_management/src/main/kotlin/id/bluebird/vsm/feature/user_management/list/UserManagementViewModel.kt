package id.bluebird.vsm.feature.user_management.list

import android.text.Editable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.user.domain.intercator.SearchUser
import id.bluebird.vsm.feature.user_management.utils.ModifyUserAction
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class UserManagementViewModel(
    private val searchUser: SearchUser,
) : ViewModel() {
    companion object {
        private const val EMPTY_STRING = ""
    }
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
            searchUser(EMPTY_STRING)
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

    fun result(name: String, action: ModifyUserAction) {
        viewModelScope.launch {
            delay(300)
            when (action) {
                is ModifyUserAction.Create -> userSettingSealed.postValue(UserSettingSealed.CreateUserSuccess(name))
                is ModifyUserAction.Edit -> userSettingSealed.postValue(UserSettingSealed.EditUserSuccess(name))
                is ModifyUserAction.Delete -> userSettingSealed.postValue(UserSettingSealed.DeleteSuccess(name))
                is ModifyUserAction.ForceLogout -> userSettingSealed.postValue(UserSettingSealed.ForceSuccess(name))
                else -> {}
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
        when (privilege.value) {
            UserUtils.SUPER, UserUtils.ADMIN -> userSettingSealed.value = UserSettingSealed.EditUser(userSettingCache)
            else -> {}
        }
    }
}