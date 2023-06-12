package id.bluebird.vsm.domain.airport_assignment.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.airport_assignment.AirportAssignmentRepository
import id.bluebird.vsm.domain.airport_assignment.DispatchFleetAirportState
import id.bluebird.vsm.domain.airport_assignment.model.DispatchFleetModel
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
import proto.AssignmentOuterClass

@ExperimentalCoroutinesApi
internal class DispatchFleetAirportCasesTest{

    private val repository : AirportAssignmentRepository = mockk()
    private lateinit var dispatchFleerAirportCases: DispatchFleetAirportCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        dispatchFleerAirportCases = DispatchFleetAirportCases(repository)
    }

    @Test
    fun `DispatchFleerAirportCasesTest, when perimeter is true`() = runTest {
        every { Hawk.get<Long>(any()) } returns 1L

        flowOf(
            dispatchFleerAirportCases.invoke(
                DispatchFleetModel(
                    subLocationId = 1L,
                    locationId = 2L,
                    isPerimeter = true,
                    withPassenger = false,
                    isArrived = false,
                    fleetsAssignment = listOf()
                )
            )
        ).test {
            //result
            Assertions.assertEquals(
                awaitItem().single(),
                DispatchFleetAirportState.WrongDispatchLocation
            )
            awaitComplete()
        }

    }

    @Test
    fun `DispatchFleerAirportCasesTest, when perimeter is false and not arrived`() = runTest {
        every { Hawk.get<Long>(any()) } returns 1L
        every {
            repository.dispatchFleetFromTerminal(
                any(), any(), any(), any(), any()
            )
        } returns flow {
            emit(
                AssignmentOuterClass.StockResponse.newBuilder()
                    .apply {
                        this.message = "aa"
                        this.stockType = "bb"
                        this.stockId = 1L
                        this.createdAt = "cc"
                    }.build()
            )
        }

        flowOf(
            dispatchFleerAirportCases.invoke(
                DispatchFleetModel(
                    subLocationId = 1L,
                    locationId = 2L,
                    isPerimeter = false,
                    withPassenger = false,
                    isArrived = false,
                    fleetsAssignment = listOf()
                )
            )
        ).test {
            //result
            assert(
                awaitItem().single() is DispatchFleetAirportState.SuccessArrived
            )
            awaitComplete()
        }

    }

    @Test
    fun `DispatchFleerAirportCasesTest, when perimeter is false and isarrived`() = runTest {
        every { Hawk.get<Long>(any()) } returns 1L
        every {
            repository.dispatchFleetFromTerminal(
                any(), any(), any(), any(), any()
            )
        } returns flow {
            emit(
                AssignmentOuterClass.StockResponse.newBuilder()
                    .apply {
                        this.message = "aa"
                        this.stockType = "bb"
                        this.stockId = 1L
                        this.createdAt = "cc"
                    }.build()
            )
        }

        flowOf(
            dispatchFleerAirportCases.invoke(
                DispatchFleetModel(
                    subLocationId = 1L,
                    locationId = 2L,
                    isPerimeter = false,
                    withPassenger = false,
                    isArrived = true,
                    fleetsAssignment = listOf()
                )
            )
        ).test {
            //result
            assert(
                awaitItem().single() is DispatchFleetAirportState.SuccessDispatchFleet
            )
            awaitComplete()
        }

    }

}