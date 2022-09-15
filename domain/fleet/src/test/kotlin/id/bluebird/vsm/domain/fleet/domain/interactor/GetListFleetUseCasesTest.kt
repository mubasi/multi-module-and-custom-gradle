package id.bluebird.vsm.domain.fleet.domain.interactor

import app.cash.turbine.test
import id.bluebird.vsm.domain.fleet.FleetRepository
import id.bluebird.vsm.domain.fleet.GetListFleetState
import id.bluebird.vsm.domain.fleet.domain.cases.GetListFleet
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import proto.AssignmentPangkalanOuterClass

@ExperimentalCoroutinesApi
internal class GetListFleetUseCasesTest {
    private val _fleetRepo: FleetRepository = mockk()
    private lateinit var _getListFleet: GetListFleet

    @BeforeEach
    fun setup() {
        _getListFleet = GetListFleetUseCases(_fleetRepo)
    }

    @Test
    fun `getListFleet, result fleets is Empty`() = runTest {
        // Mock
        every { _fleetRepo.getListFleet(any()) } returns flow {
            emit(
                AssignmentPangkalanOuterClass.GetListFleetTerminalResp.newBuilder()
                    .build()
            )
        }

        // Execute
        flowOf(_getListFleet.invoke(100)).test {
            // Result
            Assertions.assertEquals(awaitItem().single(), GetListFleetState.EmptyResult)
            awaitComplete()
        }
    }

    @Test
    fun `getListFleet, result fleets is not Empty`() = runTest {
        // Mock
        every { _fleetRepo.getListFleet(any()) } returns flow {
            emit(
                AssignmentPangkalanOuterClass.GetListFleetTerminalResp.newBuilder()
                    .addFleetList(AssignmentPangkalanOuterClass.FleetItems.newBuilder().apply {
                        createdAt = "2022-07-18T07:54:14Z"
                        fleetId = 12
                        taxiNo = "AB1212"
                    }.build())
                    .build()
            )
        }

        // Execute
        flowOf(_getListFleet.invoke(100)).test {
            // Result
            assert(awaitItem().single() is GetListFleetState.Success)
            awaitComplete()
        }
    }
}