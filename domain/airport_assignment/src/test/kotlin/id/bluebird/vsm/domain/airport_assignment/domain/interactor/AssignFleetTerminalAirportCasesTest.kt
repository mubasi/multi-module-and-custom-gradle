package id.bluebird.vsm.domain.airport_assignment.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.airport_assignment.AirportAssignmentRepository
import id.bluebird.vsm.domain.airport_assignment.AssignFleetTerminalAirportState
import id.bluebird.vsm.domain.airport_assignment.StockDepartState
import id.bluebird.vsm.domain.airport_assignment.model.AddStockDepartModel
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

internal class AssignFleetTerminalAirportCasesTest {

    private val repository : AirportAssignmentRepository = mockk()
    private lateinit var assignFleetTerminalAirportCases: AssignFleetTerminalAirportCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        assignFleetTerminalAirportCases = AssignFleetTerminalAirportCases(repository)
    }

    @Test
    fun `AddFleetAirportCasesTest, isSuccess`() = runTest {
        every { Hawk.get<Long>(any()) } returns 1L
        every {
            repository.assignFleetTerminal(
                any()
            )
        } returns flow {
            emit(
                AssignmentOuterClass.AssignFleetResponse.newBuilder()
                    .apply {
                        this.message = "aa"
                        this.totalAssignedFleet = 2L
                    }.build()
            )
        }


        flowOf(
            assignFleetTerminalAirportCases.invoke(
                AssignFleetModel(
                    subLocationId = 1L,
                    locationId = 2L,
                    withPassenger = false,
                    isArrived = false,
                    carsAssignment = listOf()
                )
            )
        ).test {
            //result
            assert(awaitItem().single() is AssignFleetTerminalAirportState.Success)
            awaitComplete()
        }

    }
}