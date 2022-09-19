package id.bluebird.vsm.domain.user.domain.usescases

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.user.UserRepository
import id.bluebird.vsm.domain.user.model.LoginParam
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
internal class LoginCaseImplTest {

    private val userRepository: UserRepository = mockk()
    private lateinit var loginCaseImpl: LoginCaseImpl


    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        loginCaseImpl = LoginCaseImpl(userRepository)
    }

    @Test
    fun `loginCase, username is Empty`() = runTest {
        //given
        val result = Throwable()
        val param = LoginParam(username = "", password = "123")

        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { userRepository.doLogin(param) } returns flow {
            emit(
                UserOuterClass.UserLoginResponse.newBuilder()
                    .apply {
                        userId = 1
                        locationId = 2
                        uuid = "aa"
                        userRole = "bb"
                        fleetType = 3
                        isAirport = true
                        accessToken = "ddd"
                    }
                    .build()
            )

            flowOf(loginCaseImpl.invoke(null, "123")).test {

                // Result
                Assertions.assertEquals(
                    this.awaitItem().single(),
                    result
                )
                awaitComplete()
            }
        }
    }

    @Test
    fun `loginCase, password is Empty`() = runTest {
        //given
        val result = Throwable()
        val param = LoginParam(username = "abc", password = "")

        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { userRepository.doLogin(param) } returns flow {
            emit(
                UserOuterClass.UserLoginResponse.newBuilder()
                    .apply {
                        userId = 1
                        locationId = 2
                        uuid = "aa"
                        userRole = "bb"
                        fleetType = 3
                        isAirport = true
                        accessToken = "ddd"
                    }
                    .build()
            )

            flowOf(loginCaseImpl.invoke("abc", null)).test {

                // Result
                Assertions.assertEquals(
                    this.awaitItem().single(),
                    result
                )
                awaitComplete()
            }
        }
    }


    @Test
    fun `loginCase, username and password is Not Empty`() = runTest {
        //given
//        val result = Throwable()
        val param = LoginParam(username = "abc", password = "123")

        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { userRepository.doLogin(param) } returns flow {
            emit(
                UserOuterClass.UserLoginResponse.newBuilder()
                    .apply {
                        userId = 1
                        locationId = 2
                        uuid = "aa"
                        userRole = "bb"
                        fleetType = 3
                        isAirport = true
                        accessToken = "ddd"
                    }
                    .build()
            )

            flowOf(loginCaseImpl.invoke("abc", "123")).test {

                // Result
                Assertions.assertEquals(
                    this.awaitItem().single(),
                    "ddd"
                )
                awaitComplete()
            }
        }
    }

}
