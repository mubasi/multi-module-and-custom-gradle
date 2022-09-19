package id.bluebird.vsm.feature.user_management.create

import id.bluebird.vsm.feature.user_management.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.extension.ExtendWith
import androidx.lifecycle.Observer
import id.bluebird.vsm.domain.location.domain.interactor.GetSubLocationByLocationId
import id.bluebird.vsm.domain.user.domain.intercator.*
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)

internal class CreateUserViewModelTest {

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
    fun `forceLogoutTest, uuid is blank`() {
        //given
        val result = Throwable()
        justRun { actionSealedObserver.onChanged(any()) }

        //execute
        createUserViewModel.forceLogout()

        //result
        verify { actionSealedObserver.onChanged(CreateUserState.OnError(result)) }

    }



}