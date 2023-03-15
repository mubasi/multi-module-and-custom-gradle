package id.bluebird.vsm.domain.user.domain.usescases

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.user.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
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

        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { userRepository.getRoles() } returns flow {
            val temp = UserOuterClass.GetRolesResponse.newBuilder()
            val roles = mutableListOf<UserOuterClass.RolesItem>()
            for (i in 0 until 3){
                roles.add(UserOuterClass.RolesItem.newBuilder().setId(i.toLong()).build())
            }
            temp.addAllRoleItems(roles)
            emit(temp.build())
        }

        // Execute
        flowOf(getRolesCases.invoke()).test {
            awaitItem().collectLatest {
                for (i in 0 until 3){
                    assert(it[i].id == i.toLong())
                }
            }
            // Result
            awaitComplete()
        }
    }
}