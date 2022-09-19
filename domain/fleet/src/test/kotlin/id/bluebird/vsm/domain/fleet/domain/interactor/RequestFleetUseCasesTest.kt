package id.bluebird.vsm.domain.fleet.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.fleet.FleetRepository
import id.bluebird.vsm.domain.fleet.RequestState
import id.bluebird.vsm.domain.fleet.domain.cases.RequestFleet
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import proto.AssignmentPangkalanOuterClass

@ExperimentalCoroutinesApi
internal class RequestFleetUseCasesTest {

    private val _repository: FleetRepository = mockk()
    private lateinit var _requestFleet: RequestFleet

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        _requestFleet = RequestFleetUseCases(_repository)
    }

    @Test
    fun `validate count is more than 0`() = runTest {
        // Mock
        every { Hawk.get<Long>(any()) } returns 1L

        // Execute
        every { _repository.requestFleet(any(), any(), any()) } returns flow {
            emit(
                AssignmentPangkalanOuterClass.RequestTaxiResponse.newBuilder()
                    .apply {
                        requestCount = 1
                    }.build()
            )
        }
        flowOf(_requestFleet.invoke(1, 0)).test {
            // Result
            Assertions.assertSame(
                this.awaitItem().first(),
                RequestState.SubLocationInvalid
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `validate subLocation is more than 0`() = runTest {
        // Mock
        every { Hawk.get<Long>(any()) } returns 1L

        // Execute
        every { _repository.requestFleet(any(), any(), any()) } returns flow {
            emit(
                AssignmentPangkalanOuterClass.RequestTaxiResponse.newBuilder()
                    .apply {
                        requestCount = 1
                    }.build()
            )
        }
        flowOf(_requestFleet.invoke(0, 2)).test {
            // Result
            Assertions.assertSame(
                this.awaitItem().first(),
                RequestState.CountInvalid
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `requestFleet, result is success and request count is one`() = runTest {
        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { _repository.requestFleet(any(), any(), any()) } returns flow {
            emit(
                AssignmentPangkalanOuterClass.RequestTaxiResponse.newBuilder()
                    .apply {
                        requestCount = 1
                    }.build()
            )
        }

        // Execute
        flowOf(_requestFleet.invoke(1, 2)).test {
            // Result
            Assertions.assertEquals(
                this.awaitItem().single(),
                RequestState.Success((1))
            )
            awaitComplete()
        }
    }
}