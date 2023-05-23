package id.bluebird.vsm.domain.airport_assignment.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.airport_assignment.AirportAssignmentRepository
import id.bluebird.vsm.domain.airport_assignment.RitaseFleetTerminalAirportState
import id.bluebird.vsm.domain.airport_assignment.model.AssignFleetModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import proto.AssignmentOuterClass

internal class RitaseFleetTerminalAirportCasesTest{

    private val repository : AirportAssignmentRepository = mockk()
    private lateinit var ritaseFleetTerminalAirportCases: RitaseFleetTerminalAirportCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        ritaseFleetTerminalAirportCases = RitaseFleetTerminalAirportCases(repository)
    }

    @Test
    fun `DispatchFleerAirportCasesTest, isSuccess`() = runTest {
        every { Hawk.get<Long>(any()) } returns 1L
        every {
            repository.ritaseFleetTerminalAirport(
                any()
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
            ritaseFleetTerminalAirportCases.invoke(
                AssignFleetModel(
                    subLocationId = 1L,
                    locationId = 2L,
                    withPassenger = false,
                    isArrived = true,
                    carsAssignment = listOf()
                )
            )
        ).test {
            //result
            assert(
                awaitItem().single() is RitaseFleetTerminalAirportState.Success
            )
            awaitComplete()
        }

    }
}