package id.bluebird.vsm.domain.user.domain.usescases

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.user.GetUserAssignmentState
import id.bluebird.vsm.domain.user.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
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
internal class GetUserAssignmentCasesTest {

    private val userRepository: UserRepository = mockk()
    private lateinit var subjectTest: GetUserAssignmentCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        mockkObject(UserUtils)
        subjectTest = GetUserAssignmentCases(userRepository)
    }

    @Test
    fun `getUserAssignmentTest when userId under zero`() = runTest {
        // Execute
        flowOf(subjectTest.invoke(0)).test {
            assert(awaitItem().single() is GetUserAssignmentState.UserNotFound)
            awaitComplete()
        }
    }

    @Test
    fun `getUserAssignmentTest when userId more zero`() = runTest {
        // Mock
        every { UserUtils.isUserOfficer() } returns false
        val list: MutableList<UserOuterClass.AssignmentLocationItems> = mutableListOf()
        for (i in 1..2) {
            val userAssignment = UserOuterClass.AssignmentLocationItems.newBuilder()
                .apply {
                    this.locationId = 1
                    this.subLocationId = i.toLong()
                    this.subLocationName = "aa.$i"
                    this.isDeposition = false
                    this.isWings = false
                    this.prefix = "aa"
                    this.locationName = "bb"
                }
                .build()
            list.add(userAssignment)
        }
        every { userRepository.getUserAssignment(any()) } returns flow {
            val temp = UserOuterClass.UserAssignmentResponse.newBuilder()
                .apply {
                    this.isAirport = false
                    this.areaCode = "aa"
                }
            list.forEach {
                temp.addSubLocationItems(it)
            }
            emit(temp.build())
        }

        // Execute
        flowOf(subjectTest.invoke(1)).test {
            assert(awaitItem().single() is GetUserAssignmentState.Success)
            awaitComplete()
        }
    }
}