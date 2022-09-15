package id.bluebird.vsm.domain.user.domain.usescases

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.user.UserDomainState
import id.bluebird.vsm.domain.user.UserRepository
import id.bluebird.vsm.domain.user.model.CreateUserParam
import id.bluebird.vsm.domain.user.model.RoleParam
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
internal class GetRolesCasesTest {

    private val userRepository: UserRepository = mockk()
    private lateinit var getRolesCases: GetRolesCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        getRolesCases = GetRolesCases(userRepository)
    }

    @Test
    fun `createUser, isSuccess`() = runTest {
        //given
        val list = mutableListOf<RoleParam>()

        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { userRepository.getRoles() } returns flow {
            emit(
                UserOuterClass.GetRolesResponse.newBuilder()
                    .build()
            )
        }

        // Execute
        flowOf(getRolesCases.invoke()).test {

            // Result
            Assertions.assertEquals(
                this.awaitItem().single(),
                list
            )
            awaitComplete()
        }
    }
}