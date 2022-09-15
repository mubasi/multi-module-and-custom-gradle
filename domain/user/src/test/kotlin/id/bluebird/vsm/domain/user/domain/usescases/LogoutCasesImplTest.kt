package id.bluebird.vsm.domain.user.domain.usescases

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.user.UserDomainState
import id.bluebird.vsm.domain.user.UserRepository
import id.bluebird.vsm.domain.user.model.CreateUserParam
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import proto.UserOuterClass

@ExperimentalCoroutinesApi
internal class LogoutCasesImplTest {

    private val userRepository: UserRepository = mockk()
    private lateinit var logoutCasesImpl: LogoutCasesImpl

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        logoutCasesImpl = LogoutCasesImpl(userRepository)
    }


    @Test
    fun `logoutUserCasesTest, isSuccess`() = runTest {
        //given
        val uuid = "abc"
        val result = Throwable()

        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { userRepository.forceLogout(uuid) } returns flow {
            emit(
                UserOuterClass.ForceLogoutResponse.newBuilder()
                    .build()
            )
        }

        // Execute
        flowOf(logoutCasesImpl.invoke(uuid)).test {

            // Result
            Assertions.assertEquals(
                this.awaitItem().single(),
                result
            )
            awaitComplete()
        }
    }

}