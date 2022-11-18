package id.bluebird.vsm.feature.user_management.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.user.SearchUserState
import id.bluebird.vsm.domain.user.domain.intercator.SearchUser
import id.bluebird.vsm.domain.user.model.SearchUserResult
import id.bluebird.vsm.domain.user.model.UserSearchParam
import id.bluebird.vsm.feature.user_management.TestCoroutineRule
import id.bluebird.vsm.feature.user_management.create.CreateUserState
import id.bluebird.vsm.feature.user_management.utils.ModifyUserAction
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
internal class UserManagementViewModelTest {

    companion object {
        private const val ERROR = "error"
    }

    private lateinit var _vm: UserManagementViewModel
    private var searchUser: SearchUser = mockk(relaxed = true)
    private lateinit var actionSealedObserver: Observer<UserSettingSealed>

    @BeforeEach
    fun setup() {
        mockkStatic(Transformations::class)
        mockkObject(UserUtils)
        actionSealedObserver = mockk()
        _vm = UserManagementViewModel(
            searchUser = searchUser
        )
        _vm.userSettingSealed.observeForever(actionSealedObserver)
    }

    @AfterEach
    fun resetEvent() {
        _vm.userSettingSealed.removeObserver(actionSealedObserver)
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

    @Test
    fun `searchUser, isFailed`() = runTest {

        // Mock
        justRun { actionSealedObserver.onChanged(any()) }
        every { searchUser.invoke("") } returns flow {
            throw NullPointerException()
        }

        // Execution
        _vm.searchUser()

        // Result
        Assertions.assertEquals(
            _vm.userSettingSealed.awaitValue(),
            UserSettingSealed.OnGetUserListProgress
        )
    }

    @Test
    fun `searchUser, isSuccess`() = runTest {
        // Given
        val result: MutableList<UserSettingCache> = ArrayList()
        for (i in 1..3) {
            result.add(
                UserSettingCache(
                    id = 1,
                    userName = "aa",
                    uuid = "bb",
                    status = true
                )
            )
        }
        _vm.getUsers(result)

        // Mock
        justRun { actionSealedObserver.onChanged(any()) }
        every { searchUser("") } returns flow {
            result.toList()
        }

        // Execution
        _vm.searchUser()

        // Result
        Assertions.assertEquals(result, _vm.resultGetUsers())
        Assertions.assertNull(_vm.searchJob())
    }

    @Test
    fun `resultTest, isCreate`() = runTest {
        // Given
        val action: ModifyUserAction = ModifyUserAction.Create
        // Mock
        justRun { actionSealedObserver.onChanged(any()) }

        // Execution
        _vm.result("aa", action)

        // Result
        Assertions.assertEquals(
            _vm.userSettingSealed.awaitValue(),
            UserSettingSealed.CreateUserSuccess("aa")
        )
        Assertions.assertNull(_vm.searchJob())
    }

    @Test
    fun `resultTest, isEdit`() = runTest {
        // Given
        val action: ModifyUserAction = ModifyUserAction.Edit
        // Mock
        justRun { actionSealedObserver.onChanged(any()) }

        // Execution
        _vm.result("aa", action)

        // Result
        Assertions.assertEquals(
            _vm.userSettingSealed.awaitValue(),
            UserSettingSealed.EditUserSuccess("aa")
        )
        Assertions.assertNull(_vm.searchJob())
    }

    @Test
    fun `resultTest, isDelete`() = runTest {
        // Given
        val action: ModifyUserAction = ModifyUserAction.Delete
        // Mock
        justRun { actionSealedObserver.onChanged(any()) }

        // Execution
        _vm.result("aa", action)

        // Result
        Assertions.assertEquals(
            _vm.userSettingSealed.awaitValue(),
            UserSettingSealed.DeleteSuccess("aa")
        )
        Assertions.assertNull(_vm.searchJob())
    }

    @Test
    fun `resultTest, isForceLogout`() = runTest {
        // Given
        val action: ModifyUserAction = ModifyUserAction.ForceLogout
        // Mock
        justRun { actionSealedObserver.onChanged(any()) }

        // Execution
        _vm.result("aa", action)

        // Result
        Assertions.assertEquals(
            _vm.userSettingSealed.awaitValue(),
            UserSettingSealed.ForceSuccess("aa")
        )
        Assertions.assertNull(_vm.searchJob())
    }

    @Test
    fun setIdleTest() {

        justRun { actionSealedObserver.onChanged(any()) }

        _vm.setIdle()

        verify { actionSealedObserver.onChanged(UserSettingSealed.Idle) }
    }

    @Test
    fun onCreateUserTest() {

        justRun { actionSealedObserver.onChanged(any()) }

        _vm.onCreateUser()

        verify { actionSealedObserver.onChanged(UserSettingSealed.CreateUser) }
    }

    @Test
    fun onEditUserTest() {
        _vm.privilege.value = UserUtils.ADMIN

        val userSettingCache = UserSettingCache(
            1, "aa", "bb", true
        )

        justRun { actionSealedObserver.onChanged(any()) }

        _vm.onEditUser(userSettingCache)

        verify { actionSealedObserver.onChanged(UserSettingSealed.EditUser(userSettingCache)) }
    }


}