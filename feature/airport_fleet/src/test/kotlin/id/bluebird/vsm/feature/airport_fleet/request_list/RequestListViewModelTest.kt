package id.bluebird.vsm.feature.airport_fleet.request_list

import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.airport_assignment.GetDetailRequestInLocationAirportState
import id.bluebird.vsm.domain.airport_assignment.domain.cases.AssignFleetTerminalAirport
import id.bluebird.vsm.domain.airport_assignment.domain.cases.GetDetailRequestByLocationAirport
import id.bluebird.vsm.domain.airport_assignment.domain.cases.GetSubLocationAirport
import id.bluebird.vsm.domain.airport_assignment.domain.cases.RitaseFleetTerminalAirport
import id.bluebird.vsm.domain.airport_assignment.model.GetDetailRequestInLocationAirportModel
import id.bluebird.vsm.domain.airport_assignment.model.SubLocationItemAirportModel
import id.bluebird.vsm.feature.airport_fleet.assign_location.AssignLocationState
import id.bluebird.vsm.feature.airport_fleet.assign_location.AssignLocationViewModel
import id.bluebird.vsm.fleet_non_apsh.TestCoroutineRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
internal class RequestListViewModelTest {


    private val getDetailRequestInLocation: GetDetailRequestByLocationAirport =
        mockk(relaxed = true)
    private lateinit var subjectUnderTest: RequestListViewModel
    private var states = mutableListOf<RequestListState>()
    private val error = "error"

    @BeforeEach
    fun setUp() {
        mockkObject(UserUtils)
        mockkStatic(Hawk::class)
        subjectUnderTest = RequestListViewModel(
            getDetailRequestInLocation
        )
    }

    @AfterEach
    fun tearDown() {
        states.clear()
    }

    @Test
    fun `initTest, when get data is Error`() = runTest {
        //given
        val result = Throwable(message = error)
        every { UserUtils.getLocationId() } returns 1L
        every { getDetailRequestInLocation.invoke(any(), any()) } returns flow {
            throw result
        }
        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        //when
        subjectUnderTest.init(false)
        runCurrent()
        delay(500)

        //then
        Assertions.assertEquals(2, states.size)
        Assertions.assertEquals(
            RequestListState.Progress,
            states[0]
        )
        Assertions.assertEquals(
            RequestListState.EmptyList(result),
            states[1]
        )
        collect.cancel()
    }


    @Test
    fun `initTest, when get data is success`() = runTest {
        //given
        val resultDomain: ArrayList<SubLocationItemAirportModel> = ArrayList()
        val result: ArrayList<FleetRequestDetail> = ArrayList()

        for (i in 1..2) {
            resultDomain.add(
                SubLocationItemAirportModel(
                    subLocationId = i.toLong(),
                    subLocationName = "aa$i",
                    count = i.toLong(),
                )
            )
            result.add(
                FleetRequestDetail(
                    "aa$i", i
                )
            )
        }

        every { UserUtils.getLocationId() } returns 1L
        every { getDetailRequestInLocation.invoke(any(), any()) } returns flow {
            emit(
                GetDetailRequestInLocationAirportState.Success(
                    GetDetailRequestInLocationAirportModel(
                        subLocationItem = resultDomain
                    )
                )
            )
        }
        val collect = launch {
            subjectUnderTest.state.toList(states)
        }

        //when
        subjectUnderTest.init(false)
        runCurrent()
        delay(500)

        //then
        Assertions.assertEquals(2, states.size)
        Assertions.assertEquals(
            RequestListState.Progress,
            states[0]
        )
        Assertions.assertEquals(
            RequestListState.Success(
                result.sortedByDescending { row -> row.requestCount }
            ),
            states[1]
        )
        collect.cancel()
    }

}