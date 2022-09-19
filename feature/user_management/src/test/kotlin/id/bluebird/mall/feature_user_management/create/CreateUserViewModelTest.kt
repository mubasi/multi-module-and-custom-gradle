package id.bluebird.mall.feature_user_management.create

import androidx.lifecycle.Observer
import com.google.common.base.CharMatcher.any
import id.bluebird.mall.domain.user.domain.intercator.*
import id.bluebird.mall.domain_location.domain.interactor.GetSubLocationByLocationId
import id.bluebird.mall.feature_user_management.TestCoroutineRule
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
class CreateUserViewModelTest {
    companion object {
        private const val ERROR = "error"
    }

    var testCoroutineRule = TestCoroutineRule()

    private lateinit var createUserViewModel: CreateUserViewModel
    private lateinit var actionSealedObserver: Observer<CreateUserState>

    private val createEditUser: CreateEditUser = mockk(relaxed = true)
    private val getRoles: GetRoles = mockk(relaxed = true)
    private val getUserId: GetUserId = mockk(relaxed = true)
    private val getSubLocationByLocationId: GetSubLocationByLocationId = mockk(relaxed = true)
    private val deleteUser: DeleteUser = mockk(relaxed = true)
    private val forceLogout: ForceLogout = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        createUserViewModel = CreateUserViewModel(
            createEditUser,
            getRoles,
            getUserId,
            getSubLocationByLocationId,
            deleteUser,
            forceLogout
        )
        actionSealedObserver = mockk()
        createUserViewModel.actionSealed.observeForever(actionSealedObserver)
    }

    @AfterEach
    fun cleanUp() {
        createUserViewModel.actionSealed.removeObserver(actionSealedObserver)
    }

    @Test
    fun onBack_actionSealedIsOnBack() = runTest {
        // Mock
        justRun { actionSealedObserver.onChanged(any()) }
//        every { actionSealedObserver.onChanged(any()) }

        // Execute
        createUserViewModel.onBack()

        // Result
        verify { actionSealedObserver.onChanged(CreateUserState.OnBack) }

        Assertions.assertEquals("123", "123")
    }
}