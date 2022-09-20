package id.bluebird.vsm.domain.location.domain.cases

import app.cash.turbine.test
import id.bluebird.vsm.domain.location.LocationDomainState
import id.bluebird.vsm.domain.location.LocationRepository
import id.bluebird.vsm.domain.location.domain.interactor.UpdateBuffer
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import proto.LocationPangkalanOuterClass

@ExperimentalCoroutinesApi
internal class UpdateBufferCasesTest {

    private val repository: LocationRepository = mockk()
    private lateinit var cases: UpdateBuffer

    @BeforeEach
    fun setUp() {
        cases = UpdateBufferCases(repository)
    }

    @Test
    fun `updateBufferCase, returns string`() = runTest {
        //GIVEN
        val testLocationId = 1L
        val testValue = 100L
        every { repository.updateBuffer(testLocationId, testValue) } returns flow {
            emit(
                LocationPangkalanOuterClass.ResponseUpdateBuffer.newBuilder().build()
            )
        }

        //WHEN
        flowOf(cases.invoke(testLocationId, testValue.toInt())).test {
            //THEN
            assertEquals(LocationDomainState.Success(""), awaitItem().singleOrNull())
            awaitComplete()
        }
    }

    @Test
    fun `updateBufferCase, returns message`() = runTest {
        //GIVEN
        val testLocationId = 1L
        val testValue = 100L
        val testMessage = "test message"
        every { repository.updateBuffer(testLocationId, testValue) } returns flow {
            emit(
                LocationPangkalanOuterClass.ResponseUpdateBuffer.newBuilder().apply {
                    this.message = testMessage
                }.build()
            )
        }

        //WHEN
        flowOf(cases.invoke(testLocationId, testValue.toInt())).test {
            //THEN
            assertEquals(LocationDomainState.Success(testMessage), awaitItem().singleOrNull())
            awaitComplete()
        }
    }
}