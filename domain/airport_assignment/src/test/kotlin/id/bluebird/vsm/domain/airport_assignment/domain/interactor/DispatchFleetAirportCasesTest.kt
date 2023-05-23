package id.bluebird.vsm.domain.airport_assignment.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.airport_assignment.AirportAssignmentRepository
import id.bluebird.vsm.domain.airport_assignment.DispatchFleetAirportState
import id.bluebird.vsm.domain.airport_assignment.StockDepartState
import id.bluebird.vsm.domain.airport_assignment.model.AddStockDepartModel
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
    private lateinit var cases: DispatchFleetAirportCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        cases = DispatchFleetAirportCases(repository)
    }

    @Test
    fun `DispatchFleerAirportCasesTest, when perimeter is true`() = runTest {
        every { Hawk.get<Long>(any()) } returns 1L

        flowOf(
            cases.invoke(
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
                        addArrivedFleet(
                            AssignmentOuterClass.ArrivedItem.newBuilder()
                                .apply {
                                    this.stockId = 1L
                                    this.taxiNo = "dd"
                                    this.createdAt = "ee"
                                }.build()
                        )
                    }.build()
            )
        }

        flowOf(
            cases.invoke(
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
            val result: HashMap<String, Long> = hashMapOf()
            result["dd"] = 1L

            Assertions.assertEquals(
                awaitItem().single(),
                DispatchFleetAirportState.SuccessArrived(
                    result
                )
            )
            awaitComplete()
        }

    }

    @Test
    fun `DispatchFleerAirportCasesTest, when perimeter is false and isArrived`() = runTest {
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
                        addTaxiNo("aa")
                    }.build()
            )
        }

        flowOf(
            cases.invoke(
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
            Assertions.assertEquals(
                awaitItem().single(),
                DispatchFleetAirportState.SuccessDispatchFleet(
                    1
                )
            )
            awaitComplete()
        }

    }

    @Test
    fun `DispatchFleerAirportCasesTest, when perimeter is false and with passenger and not arrived`() = runTest {
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
                        addArrivedFleet(
                            AssignmentOuterClass.ArrivedItem.newBuilder()
                                .apply {
                                    this.stockId = 1L
                                    this.taxiNo = "dd"
                                    this.createdAt = "ee"
                                }.build()
                        )
                    }.build()
            )
        }

        flowOf(
            cases.invoke(
                DispatchFleetModel(
                    subLocationId = 1L,
                    locationId = 2L,
                    isPerimeter = false,
                    withPassenger = true,
                    isArrived = false,
                    fleetsAssignment = listOf()
                )
            )
        ).test {
            //result
            val result: HashMap<String, Long> = hashMapOf()
            result["dd"] = 1L

            Assertions.assertEquals(
                awaitItem().single(),
                DispatchFleetAirportState.SuccessArrived(
                    result
                )
            )
            awaitComplete()
        }

    }

}