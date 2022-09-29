package id.bluebird.vsm.feature.user_management.create

import androidx.lifecycle.LiveData
import id.bluebird.vsm.feature.user_management.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.extension.ExtendWith
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.location.domain.interactor.GetSubLocationByLocationId
import id.bluebird.vsm.domain.user.GetUserByIdState
import id.bluebird.vsm.domain.user.domain.intercator.*
import id.bluebird.vsm.domain.user.model.CreateUserResult
import id.bluebird.vsm.feature.user_management.create.model.LocationAssignment
import id.bluebird.vsm.feature.user_management.list.UserSettingSealed
import id.bluebird.vsm.feature.user_management.search_location.model.Location
import io.mockk.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
internal class CreateUserViewModelTest {

    companion object {
        private const val ERROR = "error"
    }

    private lateinit var _vm: CreateUserViewModel
    private val createEditUser: CreateEditUser = mockk(relaxed = true)
    private val getRoles: GetRoles = mockk(relaxed = true)
    private val getUserId: GetUserId = mockk(relaxed = true)
    private val getSubLocationByLocationId: GetSubLocationByLocationId = mockk(relaxed = true)
    private val deleteUser: DeleteUser = mockk(relaxed = true)
    private val forceLogout: ForceLogout = mockk(relaxed = true)
    private lateinit var actionSealedObserver: Observer<CreateUserState>

    @BeforeEach
    fun setup() {
        mockkStatic(Transformations::class)
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

    private suspend fun <T> LiveData<T>.awaitValue(): T? {
        return suspendCoroutine { cont ->
            val observer = object : Observer<T> {
                override fun onChanged(t: T?) {
                    removeObserver(this)
                    cont.resume(t)
                }
            }
            observeForever(observer)
        }
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
        Assertions.assertEquals(_vm.actionSealed.awaitValue(), CreateUserState.AssignSubLocationFromData)
    }

    @Test
    fun `saveUser, proses` () = runTest {
        //given
        givenAddUserInformation()
        val result = Throwable()

        //mock
        justRun { actionSealedObserver.onChanged(any()) }
        every { createEditUser(_vm.setCreateParam()) } returns flow {
            result
        }

        //execute
        _vm.saveUser()

        //result
        Assertions.assertEquals(_vm.actionSealed.awaitValue(), CreateUserState.OnSaveProgress)
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

        // Result
        verify { actionSealedObserver.onChanged(CreateUserState.DeleteUser("aa")) }
    }


    @Test
    fun `delete, isProses` () = runTest {
        //given
        _vm.setUUID("aa")
        val result = Throwable("No UUID")

        // Mock
        justRun { actionSealedObserver.onChanged(any()) }
        every { deleteUser("aa") } returns flow {
            result
        }

        // Execute
        _vm.delete()

        // Result
        Assertions.assertEquals(_vm.actionSealed.awaitValue(), CreateUserState.OnGetDataProcess)
    }

    @Test
    fun requestForceLogoutTest() = runTest {
        //given
        _vm.userName.value = "aa"
        // Mock
        justRun { actionSealedObserver.onChanged(any()) }

        // Execute
        _vm.requestForceLogout()

        // Result
        verify { actionSealedObserver.onChanged(CreateUserState.ForceLogout("aa")) }
    }


}