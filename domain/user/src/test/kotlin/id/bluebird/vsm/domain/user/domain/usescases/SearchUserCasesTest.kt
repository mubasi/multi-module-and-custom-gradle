package id.bluebird.vsm.domain.user.domain.usescases

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.user.SearchUserState
import id.bluebird.vsm.domain.user.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import proto.UserOuterClass

@ExperimentalCoroutinesApi
internal class SearchUserCasesTest {

    private val userRepository: UserRepository = mockk()
    private lateinit var searchUserCases: SearchUserCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        searchUserCases = SearchUserCases(userRepository)
    }

    @Test
    fun `searchUserCasesTest, param is not null`() = runTest {
        //given

        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { userRepository.searchUser("abc") } returns flow {
            emit(
                UserOuterClass.SearchUserResponse.newBuilder()
                    .build()
            )
        }

        // Execute
        flowOf(searchUserCases.invoke("abc")).test {

            // Result
            assert(awaitItem().single() is SearchUserState.Success)
            awaitComplete()
        }
    }
}