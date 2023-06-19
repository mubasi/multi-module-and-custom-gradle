package id.bluebird.vsm.feature.user_management.create

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import id.bluebird.vsm.domain.location.LocationDomainState
import id.bluebird.vsm.domain.location.domain.interactor.GetSubLocationByLocationId
import id.bluebird.vsm.domain.location.model.SubLocationResult
import id.bluebird.vsm.domain.user.GetUserByIdState
import id.bluebird.vsm.domain.user.domain.intercator.*
import id.bluebird.vsm.domain.user.model.CreateUserResult
import id.bluebird.vsm.feature.user_management.TestCoroutineRule
import id.bluebird.vsm.feature.user_management.create.model.LocationAssignment
import id.bluebird.vsm.feature.user_management.create.model.RoleCache
import id.bluebird.vsm.feature.user_management.create.model.SubLocationCache
import id.bluebird.vsm.feature.user_management.search_location.model.Location
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
internal class CreateUserViewModelTest {

    companion object {
        private const val ERROR = "error"
    }

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var _vm: CreateUserViewModel
    private lateinit var actionSealedObserver: Observer<CreateUserState>
    private val createEditUser: CreateEditUser = mockk(relaxed = true)
    private val getRoles: GetRoles = mockk(relaxed = true)
    private val getUserId: GetUserId = mockk(relaxed = true)
    private val getSubLocationByLocationId: GetSubLocationByLocationId = mockk(relaxed = true)
    private val deleteUser: DeleteUser = mockk(relaxed = true)
    private val forceLogout: ForceLogout = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        actionSealedObserver = mockk()
        _vm = CreateUserViewModel(
            createEditUser,
            getRoles,
            getUserId,
            getSubLocationByLocationId,
            deleteUser,
            forceLogout
        )
        _vm.actionSealed.observeForever(actionSealedObserver)
    }

    @AfterEach
    fun resetEvent() {
        _vm.actionSealed.removeObserver(actionSealedObserver)
    }

    private fun givenAddUserInformation() {
        _vm.userName.value = "sample"
        _vm.password.value = "password"
        _vm.name.value = "name"
        val list: MutableList<LocationAssignment> = ArrayList()
        list.add(LocationAssignment(1, 2))
        _vm.setLocationAssignments(list)
    }

    @Test
    fun onBack_actionSealedIsOnBack() {
        // Mock
        justRun { actionSealedObserver.onChanged(any()) }

        // Execute
        _vm.onBack()

        // Result
        verify { actionSealedObserver.onChanged(CreateUserState.OnBack) }
    }

    @Test
    fun  `initUser, when userId not Null`() {
        _vm.setInitUser(1, "aa")
        Assertions.assertEquals(1, _vm.valMuserId())
    }

    @Test
    fun  `initUser, when userId is Null`() {
        _vm.setInitUser(null, "aa")
        Assertions.assertEquals(-1, _vm.valMuserId())
    }

    @Test
    fun  `initUser, when uuid not Null`() {
        _vm.setInitUser(1, "aa")
        Assertions.assertEquals("aa", _vm.valMuuid())
    }

    @Test
    fun  `initUser, when uuid is Null`() {
        _vm.setInitUser(null, null)
        Assertions.assertEquals("", _vm.valMuuid())
    }

    @Test
    fun  `initUser, when isCreate is True`() {
        _vm.setInitUser(1, "aa")
        Assertions.assertEquals(1, _vm.valMuserId())
        Assertions.assertEquals("aa", _vm.valMuuid())
        Assertions.assertEquals(false, _vm.isCreateNewUser.value)
    }

    @Test
    fun `getUser, if not Idle, is Failed`() = runTest {
        //given
        _vm.setInitUser(1, "aa")

        // Mock
        justRun { actionSealedObserver.onChanged(any()) }
        every { getUserId(1) } returns flow {
            throw NullPointerException()
        }

        //execute
        _vm.getUser()

        //result
//        assert(_vm.actionSealed is CreateUserState.OnError)
    }

    @Test
    fun `getUser, if not Idle, is Success`() = runTest {
        //given
        _vm.setInitUser(1, "aa")
        val tempCreteUser = CreateUserResult(
            id = 1,
            name = "aa",
            username = "bb",
            roleId = 1,
            locationId = 1,
            locationName = "cc",
            subLocationsId = listOf(1, 2),
            subLocationName = "dd"
        )

        // Mock
        justRun { actionSealedObserver.onChanged(any()) }
        every { getUserId(1) } returns flow {
            GetUserByIdState.Success(
                result = tempCreteUser
            )
        }

        //execute
        _vm.getUser()

        //result
        Assertions.assertEquals(1, _vm.valMuserId())
    }


    @Test
    fun `assignRole, if officer`() = runTest {
        //given
        val listRole = ArrayList<RoleCache>()

        for (i in 3 until 6) {
            listRole.add(RoleCache(
                id = i.toLong(),
                name = "aa $i"
            ))
        }
        _vm.setValRoles(listRole)
        _vm.mRoleId = 5

        //execute
        _vm.assignRole()

        //result
        Assertions.assertEquals(_vm.shouldShowLocation.value, true)
        Assertions.assertEquals(_vm.userRolePosition.value, 2)
    }


    @Test
    fun `assignRole, if not officer`() = runTest {
        //given
        val listRole = ArrayList<RoleCache>()

        for (i in 3 until 6) {
            listRole.add(RoleCache(
                id = i.toLong(),
                name = "aa $i"
            ))
        }
        _vm.setValRoles(listRole)
        _vm.mRoleId = 3

        //execute
        _vm.assignRole()

        //result
        Assertions.assertEquals(_vm.shouldShowLocation.value, false)
        Assertions.assertEquals(_vm.userRolePosition.value, 0)
    }

    @Test
    fun `setupSubLocationTest, when location null`() = runTest {
        _vm.setLocation(null)
        _vm.setMlocation(1)

        _vm.setupSubLocation()

        Assertions.assertEquals(_vm.valLocationAssignmentsUser().size, 0)
        Assertions.assertEquals(_vm.countSubAssignLocation.value, 0)
    }

    @Test
    fun `setupSubLocationTest, when location not null`() = runTest {
        _vm.setLocation(Location(1, "aa"))
        _vm.setMlocation(1)

        _vm.setupSubLocation()

        Assertions.assertEquals(_vm.mLocationId, 1)
        Assertions.assertEquals(_vm.shouldShowSubLocation.value, false)
    }

    @Test
    fun `setupSubLocationTest, when location not null and subLocation`() = runTest {
        _vm.setLocation(Location(1, "aa"))
        _vm.setMlocation(1)
        val subLocation = listOf(SubLocationCache(
            1, 2, "aa", true
        ))
        _vm.setSubLocations(subLocation)

        _vm.setupSubLocation()

        Assertions.assertEquals(_vm.subLocationLiveData.value, subLocation)
        Assertions.assertEquals(_vm.subLocationIndex, 0)
    }

    @Test
    fun `setupSubLocationTest, when location not null and getUserLocationAssignment Success`() = runTest {
        _vm.setLocation(Location(1, "aa"))
        _vm.setMlocation(1)
        val subLocation = listOf(SubLocationCache(
            1, 2, "aa", true
        ))
        _vm.setSubLocations(subLocation)

        // Mock
        justRun { actionSealedObserver.onChanged(any()) }
        every { getSubLocationByLocationId(1) } returns flow {
            emit(
                LocationDomainState.Success(
                    value = listOf(
                        SubLocationResult(
                            1, "aa", "bb", false, 0
                        ))
                )
            )
        }

        _vm.setupSubLocation()
        Assertions.assertEquals(_vm.getSubLocations().size, 1)
        Assertions.assertEquals(_vm.isSubLocationSelected.value, false)
    }

    @Test
    fun `setupSubLocationTest, when location not null and getUserLocationAssignment Failed`() = runTest {
        _vm.setLocation(Location(1, "aa"))
        _vm.setMlocation(1)
        val subLocation = listOf(SubLocationCache(
            1, 2, "aa", true
        ))
        _vm.setSubLocations(subLocation)

        // Mock
        justRun { actionSealedObserver.onChanged(any()) }
        every { getSubLocationByLocationId(1) } returns flow {
            throw NullPointerException()
        }

        _vm.setupSubLocation()
//        assert(_vm.actionSealed is CreateUserState.OnError)
    }

    @Test
    fun assignLocationTest() = runTest {
        //given
        val location = Location(
            id = 1,
            name = "aa",
            isSelected = true
        )
        _vm.setLocation(location)

        //mock
        justRun { actionSealedObserver.onChanged(any()) }

        //execute
        _vm.assignLocation()

        //result
        Assertions.assertEquals(location.name, _vm.selectedLocation.value)
        Assertions.assertEquals(_vm.actionSealed.value, CreateUserState.AssignSubLocationFromData)
    }

    @Test
    fun `saveUser, proses` () = runTest {
        //given
        givenAddUserInformation()

        //mock
        justRun { actionSealedObserver.onChanged(any()) }
        every { createEditUser(_vm.setCreateParam()) } returns flow {
            Throwable()
        }

        //execute
        _vm.saveUser()
        testScheduler.runCurrent()

        //result
        Assertions.assertEquals(_vm.actionSealed.value, CreateUserState.OnSaveProgress)
    }

    @Test
    fun moveToSearchLocationTest() {
        //given
        _vm.mRoleId = 1
        // Mock
        justRun { actionSealedObserver.onChanged(any()) }

        // Execute
        _vm.moveToSearchLocation()

        // Result
        verify { actionSealedObserver.onChanged(CreateUserState.RequestSearchLocation(1)) }
    }

    @Test
    fun setLocationTest() = runTest {
        //given
        val location = Location(
            id = 1,
            name = "aa",
            isSelected = true
        )
        // Mock
        justRun { actionSealedObserver.onChanged(any()) }

        // Execute
        _vm.setSelectedLocation(location)

        //result
        Assertions.assertEquals(location.name, _vm.selectedLocation.value)
        verify { actionSealedObserver.onChanged(CreateUserState.LocationSelected(location)) }
    }


    @Test
    fun toIdleTest() = runTest {
        // Mock
        justRun { actionSealedObserver.onChanged(any()) }

        // Execute
        _vm.toIdle()

        // Result
        verify { actionSealedObserver.onChanged(CreateUserState.Idle) }
    }

    @Test
    fun requestDeleteTest() = runTest {
        //given
        _vm.userName.value = "aa"
        // Mock
        justRun { actionSealedObserver.onChanged(any()) }

        // Execute
        _vm.requestDelete()
        testScheduler.runCurrent()

        // Result
        Assertions.assertEquals(_vm.actionSealed.value, CreateUserState.DeleteUser("aa"))
    }


    @Test
    fun `requestDeleteTest, Username is Null, Test`() = runTest {
        //given
        _vm.userName.value = null
        // Mock
        justRun { actionSealedObserver.onChanged(any()) }

        // Execute
        _vm.requestDelete()
        testScheduler.runCurrent()

        // Result
        Assertions.assertEquals(_vm.actionSealed.value, CreateUserState.DeleteUser(""))
    }


    @Test
    fun `delete, mUUID is blank` () = runTest {
        //given
        _vm.setInitUser(null, null)

        // Mock
        justRun { actionSealedObserver.onChanged(any()) }

        // Execute
        _vm.delete()
        runCurrent()

        // Result
        assert(_vm.actionSealed.value is CreateUserState.OnError)

    }

    @Test
    fun `delete, isFailed` () = runTest {
        //given
        val result = Throwable()
        _vm.setInitUser(1, "aa")

        // Mock
        justRun { actionSealedObserver.onChanged(any()) }
        every { deleteUser("aa") } returns flow {
            result
        }

        // Execute
        _vm.delete()
        runCurrent()

        // Result
        assert(_vm.actionSealed.value  is CreateUserState.OnGetDataProcess)

    }

    @Test
    fun requestForceLogoutTest() = runTest {
        //given
        _vm.userName.value = "aa"

        // Mock
        justRun { actionSealedObserver.onChanged(any()) }

        // Execute
        _vm.requestForceLogout()
        runCurrent()

        // Result
        Assertions.assertEquals(_vm.actionSealed.value, CreateUserState.ForceLogout("aa"))
    }

    @Test
    fun `requestForceLogout, Username is Null, Test`() = runTest {
        //given
        _vm.userName.value = null
        // Mock
        justRun { actionSealedObserver.onChanged(any()) }

        // Execute
        _vm.requestForceLogout()
        testScheduler.runCurrent()

        // Result
        Assertions.assertEquals(_vm.actionSealed.value, CreateUserState.ForceLogout(""))
    }

    @Test
    fun getInformation_progress() = runTest {
        // Given
        val result = Throwable()
        val itemList = ArrayList<SubLocationResult>()
        itemList.add(
            SubLocationResult(
                1, "aa", "bb", false, 0
            )
        )

        // Mock
        justRun { actionSealedObserver.onChanged(any()) }

        every { getSubLocationByLocationId(any()) } returns flow {
            emit(
                LocationDomainState.Success(
                    itemList
                )
            )
        }
        every { getRoles() } returns flow {
            result
        }

        // Execute
        _vm.getInformation()
        testScheduler.runCurrent()

        // Result
        Assertions.assertEquals(_vm.actionSealed.value, CreateUserState.OnGetDataProcess)
        _vm.getSubLocations().isEmpty()
        _vm.valRoles().isEmpty()
    }

    @Test
    fun `assignUserToFieldTest, when is null`() {
        val createUserResult = null

        justRun { actionSealedObserver.onChanged(any()) }

        _vm.setValassignUserToField(createUserResult)
        Assertions.assertEquals(_vm.valMuserId(), -1)
        Assertions.assertEquals(_vm.name.value, "")
        Assertions.assertEquals(_vm.userName.value, "")
        Assertions.assertEquals(_vm.mLocationId, -1)
        Assertions.assertEquals(_vm.mRoleId, -1)
        Assertions.assertEquals(_vm.isRoleSelected.value, false)
    }


    @Test
    fun `assignUserToFieldTest, when not null`() {
        val listSubLocation = ArrayList<Long>()

        for (i in 0 until 3) {
            listSubLocation.add(i.toLong())
        }

        val createUserResult = CreateUserResult(
            1, "aa", "bb", 2, 3, "cc", listSubLocation, "dd"
        )

        val tempLocation = Location(3, "cc")

        justRun { actionSealedObserver.onChanged(any()) }

        _vm.setValassignUserToField(createUserResult)
        Assertions.assertEquals(_vm.valMuserId(), 1)
        Assertions.assertEquals(_vm.name.value, "aa")
        Assertions.assertEquals(_vm.userName.value, "bb")
        Assertions.assertEquals(_vm.mLocationId, 3)
        Assertions.assertEquals(_vm.valLocation(), tempLocation)
        Assertions.assertEquals(_vm.mRoleId, 2)
        Assertions.assertEquals(_vm.isRoleSelected.value, true)
        Assertions.assertEquals(_vm.valLocationAssignmentsUser().size, listSubLocation.size)
        Assertions.assertEquals(_vm.countSubAssignLocation.value, listSubLocation.size)
    }

    @Test
    fun getInformationOnExceptionTest() = runTest {

        val result = Throwable("test")

        // Mock
        justRun { actionSealedObserver.onChanged(any()) }

        _vm.setInformationOnException(result)

        Assertions.assertEquals(_vm.subLocationLiveData.value!!.size, 0)
        Assertions.assertEquals(_vm.roleLiveData.value!!.size, 0)
        Assertions.assertEquals(_vm.actionSealed.value!!, CreateUserState.GetInformationOnError(result))
    }

    @Test
    fun onSelectedSubLocationTest() = runTest {
        var listSubLocation = listOf(SubLocationCache(
            1, 2, "aa",
        ))
        _vm.setSubLocations(listSubLocation)

        _vm.onSelectedSubLocation(0)

        Assertions.assertEquals(_vm.subLocationIndex, 0)
        Assertions.assertEquals(_vm.isSubLocationSelected.value, true)
    }

    @Test
    fun `forceLogoutTest, when is blank`() = runTest {
        //mock
        justRun { actionSealedObserver.onChanged(any()) }

        _vm.forceLogout()
        runCurrent()

        assert(_vm.actionSealed.value is CreateUserState.OnError)
    }


    @Test
    fun `forceLogoutTest, isFailed` () = runTest {
        //given
        val result = Throwable()
        _vm.setInitUser(1, "aa")

        // Mock
        justRun { actionSealedObserver.onChanged(any()) }
        every { forceLogout("aa") } returns flow {
            throw result
        }

        // Execute
        _vm.delete()
        runCurrent()

        // Result
        assert(_vm.actionSealed.value is CreateUserState.OnGetDataProcess)

    }
}
