package id.bluebird.vsm.domain.user.domain.usescases

import app.cash.turbine.test
import id.bluebird.vsm.domain.user.GetUserByIdForAssignmentState
import id.bluebird.vsm.domain.user.GetUserByIdState
import id.bluebird.vsm.domain.user.domain.intercator.GetUserId
import id.bluebird.vsm.domain.user.model.CreateUserResult
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

@ExperimentalCoroutinesApi
internal class GetUserByIdForAssignmentUsesCasesTest{

    private val getUserId = mockk<GetUserId>()
    private lateinit var subjectTest :GetUserByIdForAssignmentUsesCases

    @BeforeEach
    fun setup(){
        subjectTest = GetUserByIdForAssignmentUsesCases(getUserId = getUserId)
    }

    @Test
    fun `invoke, condition roleId is 5, result locationId and subLocationId from api`()= runTest {
        // Assert
        val locationId = 100L
        val subLocationId = 9L
        val id = 1L

        // Mock
        every { getUserId.invoke(any()) }returns (flow {
            emit(GetUserByIdState.Success(CreateUserResult(id = id, roleId = 5, locationId = locationId, subLocationsId = listOf(subLocationId,2 ,1,1))))
        })

        // Execute
        flowOf(subjectTest.invoke(userId = 1, locationIdNav = null, subLocationIdNav = null)).test {
            awaitItem().collectLatest {
               val result =  (it as GetUserByIdForAssignmentState.Success)
                assertEquals(locationId, result.result.locationId)
                assertEquals(id, result.result.id)
                assertEquals(true, result.result.isOfficer)
                assertEquals(subLocationId, result.result.subLocationId)
            }
            awaitComplete()
        }
    }

    @Test
    fun `invoke, condition roleId is not 5 and locationNav and subLocationNav is null, result locationId and subLocationId from api`()= runTest {
        // Assert
        val locationId = 100L
        val subLocationId = 9L
        val id = 1L
        val locationName = "loca"
        val subLocationName = "sub"

        // Mock
        every { getUserId.invoke(any()) }returns (flow {
            emit(GetUserByIdState.Success(CreateUserResult(id = id, roleId = 1, locationName = locationName, subLocationName =subLocationName , locationId = locationId, subLocationsId = listOf(subLocationId,2 ,1,1))))
        })

        // Execute
        flowOf(subjectTest.invoke(userId = 1, locationIdNav = null, subLocationIdNav = null)).test {
            awaitItem().collectLatest {
                val result =  (it as GetUserByIdForAssignmentState.Success)
                assertEquals(locationId, result.result.locationId)
                assertEquals(id, result.result.id)
                assertEquals(false, result.result.isOfficer)
                assertEquals(subLocationId, result.result.subLocationId)
                assertEquals(locationName, result.result.locationName)
                assertEquals(null, result.result.subLocationName)
            }
            awaitComplete()
        }
    }

    @Test
    fun `invoke, condition response is not found, result StateUserNotFound`()= runTest {
        // Mock
        every { getUserId.invoke(any()) }returns (flow {
            emit(GetUserByIdState.UserIsNotFound)
        })

        // Execute
        flowOf(subjectTest.invoke(userId = 1, locationIdNav = 2L, subLocationIdNav = 5L)).test {
            assertEquals(GetUserByIdForAssignmentState.UserNotFound, awaitItem().single())
            awaitComplete()
        }
    }

    @Test
    fun `invoke, condition roleId is not 5 and locationNav and subLocationNav available, result locationId and subLocationId from nav`()= runTest {
        // Assert
        val locationId = 100L
        val subLocationId = 9L
        val id = 1L

        // Mock
        every { getUserId.invoke(any()) }returns (flow {
            emit(GetUserByIdState.Success(CreateUserResult(id = id, roleId = 1, subLocationsId = listOf(1))))
        })

        // Execute
        flowOf(subjectTest.invoke(userId = 1, locationIdNav = locationId, subLocationIdNav = subLocationId)).test {
            awaitItem().collectLatest {
                val result =  (it as GetUserByIdForAssignmentState.Success)
                assertEquals(locationId, result.result.locationId)
                assertEquals(id, result.result.id)
                assertEquals(subLocationId, result.result.subLocationId)
            }
            awaitComplete()
        }
    }
}