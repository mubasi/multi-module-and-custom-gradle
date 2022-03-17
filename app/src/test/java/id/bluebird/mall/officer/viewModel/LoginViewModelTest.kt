package id.bluebird.mall.officer.viewModel

import androidx.lifecycle.Observer
import id.bluebird.mall.officer.common.CasesResult
import id.bluebird.mall.officer.common.CommonState
import id.bluebird.mall.officer.common.GeneralError
import id.bluebird.mall.officer.common.LoginState
import id.bluebird.mall.officer.common.uses_case.user.LoginCase
import id.bluebird.mall.officer.ui.login.LoginViewModel
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import utils.TestCoroutineRule


@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
class LoginViewModelTest {

    private val loginCase: LoginCase = mockk()

    private lateinit var mLoginVM: LoginViewModel
    private val loginStateObserver: Observer<LoginState> = mockk()

    @BeforeEach
    fun setup() {
        mLoginVM = LoginViewModel(loginCase)
        mLoginVM.ignoreStatic()
        mLoginVM.loginState.observeForever(loginStateObserver)
    }

    @AfterEach
    fun tearDown() {
        mLoginVM.loginState.removeObserver(loginStateObserver)
    }

    @Test
    fun `doLogin Result login Success`() = runTest {
        // Pre
        mLoginVM.username.value = "abc"
        mLoginVM.password.value = "abc"
        val token = "token"

        // Mock
        every { loginCase.invoke(any(), any()) } returns flow {
            emit(CasesResult.OnSuccess(token))
        }
        justRun { loginStateObserver.onChanged(any()) }

        // Execute
        mLoginVM.login()
        delay(200)

        // Result
        verify(atLeast = 1) { loginStateObserver.onChanged(CommonState.Progress) }
        verify { loginStateObserver.onChanged(LoginState.Success) }
    }

    @Test
    fun `doLogin Result login Error with GeneralError`() = runTest {
        // Pre
        mLoginVM.username.value = "abc"
        mLoginVM.password.value = "abc"
        val notFound = GeneralError.NotFound("not found")

        // Mock
        every { loginCase.invoke(any(), any()) } returns flow {
            emit(CasesResult.OnError(notFound))
        }
        justRun { loginStateObserver.onChanged(any()) }

        // Execute
        mLoginVM.login()
        delay(200)

        // Result
        verify(atLeast = 1) { loginStateObserver.onChanged(CommonState.Progress) }
        verify { loginStateObserver.onChanged(notFound) }
    }

    @Test
    fun `doLogin Result login Error with Throw Exception`() = runTest {
        // Pre
        mLoginVM.username.value = "abc"
        mLoginVM.password.value = "abc"
        val exception = NullPointerException("")

        // Mock
        every { loginCase.invoke(any(), any()) } returns flow {
            throw exception
        }
        justRun { loginStateObserver.onChanged(any()) }

        // Execute
        mLoginVM.login()
        delay(200)

        // Result
        verify(atLeast = 1) { loginStateObserver.onChanged(CommonState.Progress) }
        verify { loginStateObserver.onChanged(CommonState.Error(exception)) }
    }

    @Test
    fun `callPhone Result StatePhone is called`() = runTest {
        // Mock
        justRun { loginStateObserver.onChanged(any()) }

        // Execute
        mLoginVM.callPhone()
        delay(500)

        // Result
        verify { loginStateObserver.onChanged(LoginState.Phone) }
    }
}
