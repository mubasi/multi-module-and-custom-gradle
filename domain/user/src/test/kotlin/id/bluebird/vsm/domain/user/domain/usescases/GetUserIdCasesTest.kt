package id.bluebird.vsm.domain.user.domain.usescases

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.user.GetUserByIdState
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
internal class GetUserIdCasesTest {

    private val userRepository: UserRepository = mockk()
    private lateinit var getUserIdCases: GetUserIdCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        getUserIdCases = GetUserIdCases(userRepository)
    }

    @Test
    fun `getUserIdCasesTest, isUserId more than 0 `() = runTest {
        //given

        var userAssignmentList = mutableListOf<UserOuterClass.userAssignmentItem>()
        for (i in 1..2) {
            val userAssignment = UserOuterClass.userAssignmentItem.newBuilder()
                .apply {
                    idUser = 1
                    this.locationId = 1
                    this.subLocation = i.toLong()
                }
                .build()
            userAssignmentList.add(userAssignment)
        }

        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { userRepository.getUserById(1) } returns flow {
            emit(
                UserOuterClass.GetUserByIdResponse.newBuilder()
                    .apply {
                        name = "aa"
                        username = "bb"
                        userId = 1
                        userRole = 1
                        email = "cc"
                        createdAt = "dd"
                        createdBy = "ee"
                        userAssignmentList = userAssignmentList
                    }
                    .build()
            )
        }

        // Execute
        flowOf(getUserIdCases.invoke(1)).test {
            assert(awaitItem().single() is GetUserByIdState.Success)
            awaitComplete()
        }
    }

}