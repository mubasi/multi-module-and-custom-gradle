package id.bluebird.vsm.domain.airport_assignment.domain.interactor

import app.cash.turbine.test
import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.domain.airport_assignment.AirportAssignmentRepository
import id.bluebird.vsm.domain.airport_assignment.GetSubLocationAirportState
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

internal class GetSubLocationAirportCasesTest {

    private val repository : AirportAssignmentRepository = mockk()
    private lateinit var getSubLocationAirportCases: GetSubLocationAirportCases

    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        getSubLocationAirportCases = GetSubLocationAirportCases(repository)
    }

    @Test
    fun `GetSubLocationAirportCasesTest, isSuccess`() = runTest {
        every { Hawk.get<Long>(any()) } returns 1L
        every {
            repository.getSubLocationAssignmentByLocationId(
                any(), any(), any()
            )
        } returns flow {
            emit(
                AssignmentOuterClass.ResponseSubLocation.newBuilder()
                    .apply {

                    }.build()
            )
        }

        flowOf(
            getSubLocationAirportCases.invoke(
                1L, false, 0
            )
        ).test {
            //result
            assert(
                awaitItem().single() is GetSubLocationAirportState.Success
            )
            awaitComplete()
        }
    }

}