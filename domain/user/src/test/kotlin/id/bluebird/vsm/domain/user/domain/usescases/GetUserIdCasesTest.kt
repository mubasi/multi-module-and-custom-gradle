package id.bluebird.vsm.domain.user.domain.usescases

import app.cash.turbine.test
import id.bluebird.vsm.domain.user.GetUserByIdState
import id.bluebird.vsm.domain.user.UserRepository
import io.mockk.every
import io.mockk.mockk
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
        getUserIdCases = GetUserIdCases(userRepository)
    }

    @Test
    fun `getUserIdCasesTest, isUserId more than 0 `() = runTest {
        // Mock
        val list: MutableList<UserOuterClass.userAssignmentItem> = mutableListOf()
        for (i in 1..2) {
            val userAssignment = UserOuterClass.userAssignmentItem.newBuilder()
                .apply {
                    idUser = 1
                    this.locationId = 1
                    this.subLocation = i.toLong()
                    this.locationName = "$i"
                    this.subLocationName = "$i"
                }
                .build()
            list.add(userAssignment)
        }
        every { userRepository.getUserById(any()) } returns flow {
            val temp = UserOuterClass.GetUserByIdResponse.newBuilder()
                .apply {
                    name = "aa"
                    username = "bb"
                    userId = 1
                    userRole = 1
                    email = "cc"
                    createdAt = "dd"
                    createdBy = "ee"
                }
            list.forEach {
                temp.addUserAssignment(it)
            }
            emit(temp.build())
        }

        // Execute
        flowOf(getUserIdCases.invoke(1)).test {
            assert(awaitItem().single() is GetUserByIdState.Success)
            awaitComplete()
        }
    }

    @Test
    fun `GetUserId, condition userId less than 1, result `() = runTest {
        // Execute
        flowOf(getUserIdCases.invoke(0)).test {
            assert(awaitItem().single() is GetUserByIdState.UserIsNotFound)
            awaitComplete()
        }
    }

}