package id.bluebird.mall.officer.case

import app.cash.turbine.test
import id.bluebird.mall.officer.common.CasesResult
import id.bluebird.mall.officer.common.GeneralError
import id.bluebird.mall.officer.common.network.Retrofit
import id.bluebird.mall.officer.common.network.model.LoginResponse
import id.bluebird.mall.officer.common.repository.UserRepository
import id.bluebird.mall.officer.common.uses_case.user.LoginCase
import id.bluebird.mall.officer.common.uses_case.user.LoginCaseImpl
import id.bluebird.mall.officer.utils.ExceptionHandler
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import retrofit2.Response
import utils.TestCoroutineRule
import java.net.HttpURLConnection

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
class LoginCaseTest {

    private lateinit var mLoginCase: LoginCase
    private val mUserRepository: UserRepository = mockk()

    @BeforeEach
    fun setup() {
        mLoginCase = LoginCaseImpl(mUserRepository)
    }

    @Test
    fun `login Given username is not null and password is not null Result login success`() =
        runTest {
            // result
            val token = "dawdjaoidjoaiwd"

            // Mock
            coEvery {
                mUserRepository.userLogin(any())
            } returns Response.success(LoginResponse(accessToken = token))

            // Execute
            mLoginCase.invoke("used", "used")
                .test {

                    // Result
                    assertEquals(CasesResult.OnSuccess(token), this.awaitItem())
                    awaitComplete()
                }
        }

    @Test
    fun `login Given username is null and password is not null Result ThrowNullPointerException`() =
        runTest {
            // Execute
            mLoginCase.invoke(null, "").test {
                val error = awaitError()
                val assert = assertThrows(
                    NullPointerException::class.java,
                    {
                        throw error
                    },
                    "Username cannot be null"
                )

                // Result
                assertEquals(LoginCaseImpl.USERNAME_EMPTY, assert.message)
            }
        }

    @Test
    fun `login Given username is empty and password is not null Result ThrowNullPointerException`() =
        runTest {
            // Execute
            mLoginCase.invoke("", "").test {
                val error = awaitError()
                val assert = assertThrows(
                    NullPointerException::class.java,
                    {
                        throw error
                    },
                    "Username cannot be empty"
                )

                // Result
                assertEquals(LoginCaseImpl.USERNAME_EMPTY, assert.message)
            }
        }

    @Test
    fun `login Given username is not null and password is null Result ThrowNullPointerException`() =
        runTest {
            // Execute
            mLoginCase.invoke("used", null).test {
                val error = awaitError()
                val assert = assertThrows(
                    NullPointerException::class.java,
                    {
                        throw error
                    },
                    "Password cannot be null"
                )

                // Result
                assertEquals(LoginCaseImpl.PASSWORD_EMPTY, assert.message)
            }
        }

    @Test
    fun `login Given username is not null and password is empty Result ThrowNullPointerException`() =
        runTest {
            // Execute
            mLoginCase.invoke("used", "").test {
                val error = awaitError()
                val assert = assertThrows(
                    NullPointerException::class.java,
                    {
                        throw error
                    },
                    "Password cannot be empty"
                )

                // Result
                assertEquals(LoginCaseImpl.PASSWORD_EMPTY, assert.message)
            }
        }

    @Test
    fun `login Given username is not null and password is not null Result ThrowNullPointerException`() =
        runTest {
            // Mock
            coEvery {
                mUserRepository.userLogin(any())
            } returns Response.success(null)

            // Execute
            mLoginCase.invoke("used", "used").test {
                val error = awaitError()
                val assert = assertThrows(
                    NullPointerException::class.java,
                    {
                        throw error
                    },
                    "ResponseBody cannot null"
                )

                // Result
                assertEquals(ExceptionHandler.RESPONSE_BODY_IS_NULL, assert.message)
            }
        }

    @Test
    fun `login Given username is not null and password is not null Result Error Not Found`() =
        runTest {
            // Value
            val officerNotFoundMessage = "officer not found"
            val json = JSONObject()
            json.put("message", officerNotFoundMessage)
            val responseMock =
                json.toString().toResponseBody(Retrofit.CONTENT_TYPE_CHARSET.toMediaType())

            // Mock
            coEvery {
                mUserRepository.userLogin(any())
            } returns Response.error(
                HttpURLConnection.HTTP_NOT_FOUND,
                responseMock
            )

            // Execute
            mLoginCase.invoke("used", "used").test {

                // Result
                assertEquals(
                    CasesResult.OnError(GeneralError.NotFound(officerNotFoundMessage)),
                    awaitItem()
                )
                awaitComplete()
            }
        }
}