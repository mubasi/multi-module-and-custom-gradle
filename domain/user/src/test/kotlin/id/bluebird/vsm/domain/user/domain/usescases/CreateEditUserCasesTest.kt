package id.bluebird.vsm.domain.user.domain.usescases

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.user.UserDomainState
import id.bluebird.vsm.domain.user.UserErr
import id.bluebird.vsm.domain.user.UserRepository
import id.bluebird.vsm.domain.user.model.CreateUserParam
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
internal class CreateEditUserCasesTest {

    private val userRepository: UserRepository = mockk()
    private lateinit var createEditUserCases: CreateEditUserCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        createEditUserCases = CreateEditUserCases(userRepository)
    }

    @Test
    fun `createUser, isSuccess`() = runTest {
        //given
        var listSubLocation = ArrayList<Long>()
        listSubLocation.add(1)

        var userParam = CreateUserParam(
            id = 0,
            name = "aa",
            username = "bb",
            password = "cc",
            newPassword = "cc",
            roleId = 1,
            locationId = 2,
            subLocationsId = listSubLocation
        )

        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { userRepository.createUser(userParam) } returns flow {
            emit(
                UserOuterClass.CreateUserResponse.newBuilder()
                    .apply {
                        username = "bb"
                    }
                    .build()
            )
        }

        // Execute
        flowOf(createEditUserCases.invoke(userParam)).test {

            // Result
            Assertions.assertEquals(
                this.awaitItem().single(),
                UserDomainState.Success("bb")
            )
            awaitComplete()
        }
    }

    @Test
    fun `editUser, isSuccess`() = runTest {
        //given
        var listSubLocation = ArrayList<Long>()
        listSubLocation.add(1)

        var userParam = CreateUserParam(
            id = 1,
            name = "aa",
            username = "bb",
            password = "cc",
            newPassword = "cc",
            roleId = 1,
            locationId = 2,
            subLocationsId = listSubLocation
        )

        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { userRepository.editUser(userParam) } returns flow {
            emit(
                UserOuterClass.EditUserResponse.newBuilder()
                    .build()
            )
        }

        // Execute
        flowOf(createEditUserCases.invoke(userParam)).test {

            // Result
            Assertions.assertEquals(
                this.awaitItem().single(),
                UserDomainState.Success("bb")
            )
            awaitComplete()
        }
    }

}