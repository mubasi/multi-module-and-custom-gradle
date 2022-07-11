package id.bluebird.mall.feature_user_management.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bluebird.mall.domain.user.GetUserByIdState
import id.bluebird.mall.domain.user.UserDomainState
import id.bluebird.mall.domain.user.domain.intercator.CreateEditUser
import id.bluebird.mall.domain.user.domain.intercator.GetRoles
import id.bluebird.mall.domain.user.domain.intercator.GetUserById
import id.bluebird.mall.domain.user.model.CreateUserParam
import id.bluebird.mall.domain.user.model.CreateUserResult
import id.bluebird.mall.domain_location.LocationDomainState
import id.bluebird.mall.domain_location.domain.interactor.GetSubLocationByLocationId
import id.bluebird.mall.feature_user_management.create.model.LocationAssignment
import id.bluebird.mall.feature_user_management.create.model.RoleCache
import id.bluebird.mall.feature_user_management.create.model.SubLocationCache
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CreateUserViewModel(
    private val createEditUser: CreateEditUser,
    private val getRoles: GetRoles,
    private val getUserById: GetUserById,
    private val getSubLocationByLocationId: GetSubLocationByLocationId
) : ViewModel() {

    companion object {
        private const val OFFICER_ROLE_ID = 5L
        private const val DEFAULT_ROLE_NAME = "Pilih Role"
    }

    val name: MutableLiveData<String> = MutableLiveData()
    val userName: MutableLiveData<String> = MutableLiveData()
    val password: MutableLiveData<String> = MutableLiveData()
    val oldPassword: MutableLiveData<String> = MutableLiveData()
    val actionSealed: MutableLiveData<CreateUserState> = MutableLiveData()
    val countSubAssignLocation: MutableLiveData<Int> = MutableLiveData(0)
    var isRoleSelected: MutableLiveData<Boolean> = MutableLiveData()
    val subLocationSingleSelection: MutableLiveData<Boolean> = MutableLiveData(true)
    val subLocationLiveData: MutableLiveData<List<SubLocationCache>> = MutableLiveData()
    val roleLiveData: MutableLiveData<List<RoleCache>> = MutableLiveData()
    val userRolePosition: MutableLiveData<Int> = MutableLiveData(-1)
    var isCreateNewUser: MutableLiveData<Boolean> = MutableLiveData(false)

    private var mLocationId: Long = -1
    private var mRoleId: Long = -1
    private val subLocations: MutableList<SubLocationCache> = ArrayList()
    private val roles: MutableList<RoleCache> = ArrayList()
    private var mUserId: Long = -1
    private val locationAssignmentsUser: HashMap<Long, LocationAssignment> = HashMap()
    private val coroutineException = CoroutineExceptionHandler { _, e ->
        getInformationOnException(e)
    }

    fun initUser(userId: Long?) {
        mUserId = userId ?: -1
        actionSealed.value = null
        isCreateNewUser.postValue(mUserId < 1)
    }

    fun getInformation() {
        val job = viewModelScope.launch(coroutineException) {
            actionSealed.postValue(CreateUserState.OnGetDataProcess)
            val role = async { getUserRole() }
            val subLocation = async { getUserLocationAssignment() }
            role.await()
            subLocation.await()
        }
        job.invokeOnCompletion {
            if (it == null) {
                subLocationLiveData.postValue(subLocations)
                roleLiveData.postValue(roles)
                actionSealed.postValue(CreateUserState.GetInformationSuccess)
            }
        }
    }

    fun getUser() {
        viewModelScope.launch {
            getUserById.invoke(mUserId)
                .catch { cause: Throwable ->

                }
                .collect {
                    when (it) {
                        is GetUserByIdState.Success -> {
                            assignUserToField(it.result)
                        }
                        GetUserByIdState.UserIdIsWrong -> {
                            assignUserToField(null)
                        }
                    }
                }
        }
    }

    private fun assignUserToField(value: CreateUserResult?) {
        mUserId = value?.id ?: -1
        name.postValue(value?.name ?: "")
        userName.postValue(value?.username ?: "")
        mLocationId = value?.locationId ?: -1
        mRoleId = value?.roleId ?: -1
        isRoleSelected.postValue(value != null)
        value?.let {
            assignSubLocation(value.subLocationsId)
        }
        actionSealed.postValue(CreateUserState.GetUserStateSuccess)
    }

    private fun assignSubLocation(
        subLocationsId: List<Long>
    ) {
        locationAssignmentsUser.clear()
        subLocationsId.forEach {
            val locationAssignment = LocationAssignment(mLocationId, it)
            locationAssignmentsUser[it] = locationAssignment
        }
        countSubAssignLocation.postValue(locationAssignmentsUser.size)
    }

    fun addSubLocation() {
        actionSealed.value = CreateUserState.GetSubLocation
        subLocations.forEach {
            if (locationAssignmentsUser[it.id] != null) {
                it.isSelected = true
            }
        }
        subLocationLiveData.value = subLocations
        actionSealed.value = null
    }

    fun assignRole() {
        for (i in 0 until roles.size) {
            val role = roles[i]
            if (role.id == mRoleId) {
                subLocationSingleSelection.value = role.id == OFFICER_ROLE_ID
                userRolePosition.postValue(i)
                break
            }
        }
    }

    private suspend fun getUserRole() {
        getRoles.invoke()
            .catch { cause: Throwable ->
                actionSealed.postValue(CreateUserState.OnError(cause))
            }
            .collect {
                roles.clear()
                val items = it
                if (mRoleId < 1) {
                    roles.add(RoleCache(-1, DEFAULT_ROLE_NAME))
                }
                if (items.isNotEmpty()) {
                    items.forEach { role ->
                        val roleCache = RoleCache(role.id, role.name)
                        roles.add(roleCache)
                    }
                }
            }
    }

    private suspend fun getUserLocationAssignment() {
        getSubLocationByLocationId.invoke(mLocationId)
            .catch { cause: Throwable ->
                actionSealed.postValue(CreateUserState.OnError(cause))
            }
            .collect {
                when (it) {
                    is LocationDomainState.Success -> {
                        subLocations.clear()
                        val item = it.value
                        item.forEach { subLocation ->
                            val subLocationCache =
                                SubLocationCache(subLocation.id, mLocationId, subLocation.name)
                            subLocations.add(subLocationCache)
                        }
                    }
                }

            }
    }

    private fun getInformationOnException(it: Throwable) {
        subLocationLiveData.postValue(ArrayList())
        roleLiveData.postValue(ArrayList())
        actionSealed.postValue(CreateUserState.GetInformationOnError(it))
    }

    fun locationAssignment(objects: Any, isAdd: Boolean) {
        if (isAdd) {
            addLocation(objects)
        } else {
            locationAssignmentsUser.remove(objects.toString().toLong())
        }
        countSubAssignLocation.value = locationAssignmentsUser.size
    }

    private fun addLocation(objects: Any) {
        if (subLocationSingleSelection.value == true) {
            locationAssignmentsUser.clear()
        }
        for (i in 0 until subLocations.size) {
            val value = subLocations[i]
            if (value.id == objects.toString().toLong()) {
                val locationAssignment = LocationAssignment(
                    value.locationId, value.id
                )
                locationAssignmentsUser[value.id] = locationAssignment
                break
            }
        }
    }

    fun saveUser() {
        viewModelScope.launch {
            actionSealed.postValue(CreateUserState.OnSaveProgress)
            createEditUser.invoke(createParam())
                .catch { cause ->
                    actionSealed.postValue(CreateUserState.OnError(cause))
                }
                .collect {
                    when (it) {
                        is UserDomainState.Success -> {
                            actionSealed.postValue(
                                CreateUserState.OnSuccess(
                                    it.value,
                                    mUserId < 1
                                )
                            )
                        }
                    }
                }
        }
    }

    private fun createParam(): CreateUserParam {
        val list = mutableListOf<Long>()
        locationAssignmentsUser.forEach { (l, _) ->
            list.add(l)
        }
        return CreateUserParam(
            name = name.value,
            username = name.value,
            newPassword = password.value,
            password = oldPassword.value ?: "",
            locationId = locationAssignmentsUser.values.first().locationId,
            roleId = mRoleId,
            subLocationsId = list,
            id = mUserId
        )
    }

    private fun selectedAllSubLocation() {
        subLocations.forEach {
            locationAssignmentsUser[it.id] = LocationAssignment(it.locationId, it.id)
        }
    }

    fun onBack() {
        actionSealed.value = CreateUserState.OnBack
    }

    fun onSelectedItem(position: Int) {
        val roleCache = roles[position]
        isRoleSelected.value = roleCache.id > 0
        if (roleCache.id != mRoleId) {
            mRoleId = roleCache.id
            subLocationSingleSelection.value = roleCache.id == 5.toLong()
            if (roleCache.id != 5.toLong()) {
                selectedAllSubLocation()
            } else {
                locationAssignmentsUser.clear()
            }
            countSubAssignLocation.value = locationAssignmentsUser.size
        }
    }
}