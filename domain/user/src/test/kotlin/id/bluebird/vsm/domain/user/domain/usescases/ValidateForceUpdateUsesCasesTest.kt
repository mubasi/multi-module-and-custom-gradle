package id.bluebird.vsm.domain.user.domain.usescases

import app.cash.turbine.test
import id.bluebird.vsm.domain.user.UserRepository
import id.bluebird.vsm.domain.user.ValidateForceUpdateState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import proto.UserOuterClass.SplashConfigResponse

@ExperimentalCoroutinesApi
internal class ValidateForceUpdateUsesCasesTest {
    private val userRepository = mockk<UserRepository>()
    private lateinit var subjectTest:ValidateForceUpdateUsesCases

    @BeforeEach
    fun setup(){
        subjectTest = ValidateForceUpdateUsesCases(userRepository = userRepository)
    }

    @Test
    fun `invoke, condition codeVersion is null, result CodeVersionNotFound`() = runTest{
        // Execute
        flowOf(subjectTest.invoke("", null)).test {
            assertEquals(ValidateForceUpdateState.CodeVersionNotFound, awaitItem().single())
            awaitComplete()
        }
    }

    @Test
    fun `invoke, condition codeVersion is less than current version, result NotFoundNewVersion`() = runTest{
        // Mock
        every { userRepository.getSplashConfig(any()) }returns (flow {
            emit(SplashConfigResponse.newBuilder().setPlayStoreUrl("aab").setVersionCode(99).build())
        })

        // Execute
        flowOf(subjectTest.invoke("", 100)).test {
            assertEquals(ValidateForceUpdateState.NotFoundNewVersion, awaitItem().single())
            awaitComplete()
        }
    }


    @Test
    fun `invoke, condition playStoreUrl is empty, result FoundNewVersion`() = runTest{
        val versionName = "aaa"
        val playStoreUrl = "bbb"

        // Mock
        every { userRepository.getSplashConfig(any()) }returns (flow {
           emit( SplashConfigResponse.newBuilder().apply {
                this.versionName = versionName
                this.playStoreUrl = playStoreUrl
                this.versionCode = 101
            }.build())
        })

        // Execute
        flowOf(subjectTest.invoke("", 100)).test {
            awaitItem().collectLatest {
                val result = it as ValidateForceUpdateState.FoundNewVersion
                assertEquals(versionName, result.versionName)
                assertEquals(playStoreUrl, result.url )
            }
            awaitComplete()
        }
    }
}