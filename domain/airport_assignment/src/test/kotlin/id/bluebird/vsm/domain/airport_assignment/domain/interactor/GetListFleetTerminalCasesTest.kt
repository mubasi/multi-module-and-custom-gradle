package id.bluebird.vsm.domain.airport_assignment.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.airport_assignment.AirportAssignmentRepository
import id.bluebird.vsm.domain.airport_assignment.GetListFleetTerminalDepartState
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import proto.AssignmentOuterClass


@ExperimentalCoroutinesApi
internal class GetListFleetTerminalCasesTest {
    private val repository : AirportAssignmentRepository = mockk()
    private lateinit var getListFleetTerminalCases: GetListFleetTerminalCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        getListFleetTerminalCases = GetListFleetTerminalCases(repository)
    }


    @Test
    fun `GetListFleetTerminalCases, when list is empty`() = runTest {
        every { Hawk.get<Long>(any()) } returns 1L
        every {
            repository.getListFleetTerminalDepart(
                any(), any(), any(),
            )
        } returns flow {
            emit(
                AssignmentOuterClass.GetListFleetTerminalResp.newBuilder().build()
            )
        }

        flowOf(
            getListFleetTerminalCases.invoke(1L, 2, 3)
        ).test {
            assert(awaitItem().single() is GetListFleetTerminalDepartState.EmptyResult)
            awaitComplete()
        }
    }

    @Test
    fun `GetListFleetTerminalCases, isSuccess`() = runTest {
        every { Hawk.get<Long>(any()) } returns 1L
        every {
            repository.getListFleetTerminalDepart(
                any(), any(), any(),
            )
        } returns flow {
            emit(
                AssignmentOuterClass.GetListFleetTerminalResp.newBuilder()
                    .apply {
                        addFleetList(
                            AssignmentOuterClass.FleetItems.newBuilder()
                                .apply {
                                    this.fleetId = 1
                                    this.taxiNo = "aa"
                                    this.createdAt = "2013-06-28T22:15:00Z"
                                    this.status = "cc"
                                }.build()
                        )
                    }.build()
            )
        }

        flowOf(
            getListFleetTerminalCases.invoke(1L, 2, 3)
        ).test {
            assert(awaitItem().single() is GetListFleetTerminalDepartState.Success)
            awaitComplete()
        }
    }

}