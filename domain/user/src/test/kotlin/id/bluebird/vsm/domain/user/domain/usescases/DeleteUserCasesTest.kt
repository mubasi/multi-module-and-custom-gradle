package id.bluebird.vsm.domain.user.domain.usescases

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.user.UserDomainState
import id.bluebird.vsm.domain.user.UserRepository
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
internal class DeleteUserCasesTest {

    private val userRepository: UserRepository = mockk()
    private lateinit var deleteUserCases: DeleteUserCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        deleteUserCases = DeleteUserCases(userRepository)
    }

    @Test
    fun `deleteUserCasesTest, isSuccess` () = runTest {

        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { userRepository.deleteUser("1", 1) } returns flow {
            emit(
                UserOuterClass.DeleteUserResponse.newBuilder()
                    .build()
            )
        }

        // Execute
        flowOf(deleteUserCases.invoke("1")).test {

            // Result
            Assertions.assertEquals(
                this.awaitItem().single(),
                true
            )
            awaitComplete()
        }
    }

}