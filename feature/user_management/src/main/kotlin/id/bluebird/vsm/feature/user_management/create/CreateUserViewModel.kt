package id.bluebird.vsm.feature.user_management.create

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import id.bluebird.vsm.domain.user.GetUserByIdState
import id.bluebird.vsm.domain.user.UserDomainState
import id.bluebird.vsm.domain.user.domain.intercator.*
import id.bluebird.vsm.domain.user.model.CreateUserParam
import id.bluebird.vsm.domain.user.model.CreateUserResult
import id.bluebird.vsm.domain.location.LocationDomainState
import id.bluebird.vsm.domain.location.domain.interactor.GetSubLocationByLocationId
import id.bluebird.vsm.feature.user_management.create.model.LocationAssignment
import id.bluebird.vsm.feature.user_management.create.model.RoleCache
import id.bluebird.vsm.feature.user_management.create.model.SubLocationCache
import id.bluebird.vsm.feature.user_management.search_location.model.Location
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CreateUserViewModel(
    private val createEditUser: CreateEditUser,
    private val getRoles: GetRoles,
    private val getUserId: GetUserId,
    private val getSubLocationByLocationId: GetSubLocationByLocationId,
    private val deleteUser: DeleteUser,
    private val forceLogout: ForceLogout,
) : ViewModel() {

    companion object {
        private const val OFFICER_ROLE_ID = 5L
        private const val DEFAULT_ROLE_NAME = "Pilih Role"
        private const val DEFAULT_SUB_LOCATION_NAME = "Pilih sub-lokasi"
        private const val EMPTY_STRING = ""
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
    val selectedLocation: MutableLiveData<String> = MutableLiveData(EMPTY_STRING)
    val shouldShowLocation: MutableLiveData<Boolean> = MutableLiveData(false)
    val shouldShowSubLocation: MutableLiveData<Boolean> = MutableLiveData(false)
    val subLocationPosition: MutableLiveData<Int> = MutableLiveData(-1)
    val isSubLocationSelected: MutableLiveData<Boolean> = MutableLiveData(false)

    private var mLocationId: Long = -1
    var mRoleId: Long = -1
    private val subLocations: MutableList<SubLocationCache> = ArrayList()
    private val roles: MutableList<RoleCache> = ArrayList()
    private var rolePosition: Int = 0
    private var subLocationIndex: Int = 0
    private var mUserId: Long = -1
    var locationAssignmentsUser: HashMap<Long, LocationAssignment> = HashMap()
    private var location: Location? = null
    private var mUUID: String = EMPTY_STRING
    private val coroutineException = CoroutineExceptionHandler { _, e ->
        getInformationOnException(e)
    }

    @VisibleForTesting
    fun setLocationAssignments(list: List<LocationAssignment>) {
        locationAssignmentsUser.putAll(list.associateBy { it.subLocationId })
    }

    @VisibleForTesting
    fun setInitUser(userId: Long?, uuid: String?) {
        initUser(userId, uuid)
    }

    @VisibleForTesting
    fun valMuserId(): Long {
        return mUserId
    }

    @VisibleForTesting
    fun setLocation(tempLocation: Location) {
        location = tempLocation
    }

    @VisibleForTesting
    fun setCreateParam(): CreateUserParam {
        return createParam()
    }

    @VisibleForTesting
    fun setUUID(uuid: String?) {
        mUUID = uuid ?: EMPTY_STRING
    }

    fun initUser(userId: Long?, uuid: String?) {
        mUserId = userId ?: -1
        mUUID = uuid ?: EMPTY_STRING
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
                viewModelScope.launch {
                    delay(500)
                    actionSealed.postValue(CreateUserState.GetInformationSuccess)
                }
            }
        }
    }

    fun getUser() {
        if (actionSealed.value is CreateUserState.Idle) {
            assignRole()
            return
        }
        viewModelScope.launch {
            getUserId.invoke(mUserId)
                .catch { cause ->
                 actionSealed.postValue(CreateUserState.OnError(cause))
                }
                .collect {
                    when (it) {
                        is GetUserByIdState.Success -> {
                            assignUserToField(it.result)
                        }
                    }
                }
        }
    }

    private fun assignUserToField(value: CreateUserResult?) {
        mUserId = value?.id ?: -1
        name.postValue(value?.name ?: EMPTY_STRING)
        userName.postValue(value?.username ?: EMPTY_STRING)
        mLocationId = value?.locationId ?: -1
        mRoleId = value?.roleId ?: -1
        isRoleSelected.postValue(value != null)
        value?.let {
            if (mLocationId > 0)
                location = Location(mLocationId, it.locationName)
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

    private fun addSubLocation() {
        subLocations.forEach {
            if (locationAssignmentsUser[it.id] != null) {
                it.isSelected = false
                if (actionSealed.value is CreateUserState.AssignSubLocationFromData) {
                    it.isSelected = true
                }
            }
        }
        subLocationLiveData.value = subLocations
        subLocations.indexOfFirst { it.isSelected }.let {
            if (it > 0)
                subLocationIndex = it
        }
    }

    fun assignRole() {
        for (i in 0 until roles.size) {
            val role = roles[i]
            if (role.id == mRoleId) {
                shouldShowLocation.value = role.id == OFFICER_ROLE_ID
                userRolePosition.postValue(i)
                break
            }
        }
    }

    fun assignLocation() {
        location?.let {
            selectedLocation.value = it.name
            shouldShowSubLocation.value = selectedLocation.value?.isNotBlank() == true
        }
        actionSealed.value = CreateUserState.AssignSubLocationFromData
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
                        subLocations.add(SubLocationCache(-1, -1, DEFAULT_SUB_LOCATION_NAME))
                        isSubLocationSelected.value = false
                        item.forEach { subLocation ->
                            val subLocationCache =
                                SubLocationCache(subLocation.id, mLocationId, subLocation.name)
                            subLocations.add(subLocationCache)
                        }
                    }
                    else -> {
                        // do nothing
                    }
                }

            }
    }

    private fun getInformationOnException(it: Throwable) {
        subLocationLiveData.postValue(ArrayList())
        roleLiveData.postValue(ArrayList())
        actionSealed.postValue(CreateUserState.GetInformationOnError(it))
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
                        else -> {
                            // do nothing
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
            password = oldPassword.value ?: EMPTY_STRING,
            locationId = locationAssignmentsUser.values.first().locationId,
            roleId = mRoleId,
            subLocationsId = list,
            id = mUserId
        )
    }

    private fun selectedAllSubLocation() {
        subLocations.forEach {
            if (it.id > 0)
                locationAssignmentsUser[it.id] = LocationAssignment(it.locationId, it.id)
        }
    }

    fun onBack() {
        actionSealed.value = CreateUserState.OnBack
    }

    fun onSelectedItem(position: Int) {
        if (actionSealed.value is CreateUserState.Idle){
            actionSealed.value = null
            userRolePosition.postValue(rolePosition)
            return
        }
        val roleCache = roles[position]
        isRoleSelected.value = roleCache.id > 0
        rolePosition = position

        if (actionSealed.value is CreateUserState.OnGetDataProcess)
            return

        if (roleCache.id != mRoleId) {
            mRoleId = roleCache.id
            shouldShowLocation.value = roleCache.id == OFFICER_ROLE_ID
            if (roleCache.id != 5.toLong()) {
                selectedAllSubLocation()
            } else {
                selectedLocation.value = EMPTY_STRING
                location = null
                locationAssignmentsUser.clear()
            }
            countSubAssignLocation.value = locationAssignmentsUser.size
        }
    }

    fun moveToSearchLocation() {
        actionSealed.value = CreateUserState.RequestSearchLocation(mRoleId)
    }

    fun setSelectedLocation(location: Location?) {
        this.location = location
        selectedLocation.value = this.location?.name ?: EMPTY_STRING
        actionSealed.postValue(CreateUserState.LocationSelected(location))
    }

    fun onSelectedSubLocation(position: Int) {
        if (actionSealed.value is CreateUserState.AssignSubLocationFromData || actionSealed.value is CreateUserState.Idle) {
            actionSealed.value = null
            subLocationPosition.value = subLocationIndex
            return
        }

        val subLocation = subLocations[position]
        subLocationIndex = position
        isSubLocationSelected.value = subLocation.id > 0
        if (isSubLocationSelected.value == true)
            assignSubLocation(listOf(subLocation.id))
    }

    fun toIdle() {
        actionSealed.value = CreateUserState.Idle
    }

    fun setupSubLocation() {
        if (location == null || mLocationId != this.location?.id) {
            assignSubLocation(arrayListOf())
        }
        mLocationId = location?.id ?: -1
        viewModelScope.launch { getUserLocationAssignment() }
        addSubLocation()
        shouldShowSubLocation.value = mLocationId > 0 && (mRoleId == OFFICER_ROLE_ID)
    }

    fun requestDelete() {
        viewModelScope.launch {
            actionSealed.postValue(CreateUserState.DeleteUser(userName.value ?: EMPTY_STRING))
        }
    }

    fun delete() {
        if (mUUID.isBlank()) {
            viewModelScope.launch {
                actionSealed.postValue(CreateUserState.OnError(Throwable("No UUID")))
            }
            return
        }
        viewModelScope.launch {
            actionSealed.postValue(CreateUserState.OnGetDataProcess)
            deleteUser.invoke(mUUID)
                .catch { cause: Throwable ->
                    actionSealed.postValue(CreateUserState.OnError(cause))
                }
                .collect {
                    actionSealed.postValue(CreateUserState.OnSuccessDeleteUser(userName.value.toString()))
                }
        }
    }

    fun requestForceLogout() {
        viewModelScope.launch {
            actionSealed.postValue(CreateUserState.ForceLogout(userName.value ?: EMPTY_STRING))
        }
    }

    fun forceLogout() {
        if (mUUID.isBlank()) {
            viewModelScope.launch {
                actionSealed.postValue(CreateUserState.OnError(Throwable("No UUID")))
            }
            return
        }
        viewModelScope.launch {
            actionSealed.postValue(CreateUserState.OnGetDataProcess)
            forceLogout.invoke(mUUID)
                .catch { cause: Throwable ->
                    actionSealed.postValue(CreateUserState.OnError(cause))
                }
                .collect {
                    actionSealed.postValue(CreateUserState.OnSuccessForceLogout(userName.value ?: EMPTY_STRING))
                }
        }
    }

    fun addData(first: Int, second: Int) : Int {
        return first + second
    }
}