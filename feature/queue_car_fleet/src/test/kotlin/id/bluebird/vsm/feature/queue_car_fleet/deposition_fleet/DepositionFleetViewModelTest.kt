package id.bluebird.vsm.feature.queue_car_fleet.deposition_fleet

import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.core.extensions.StringExtensions.convertCreateAtValue
import id.bluebird.vsm.domain.fleet.GetListFleetState
import id.bluebird.vsm.domain.fleet.domain.cases.GetListFleet
import id.bluebird.vsm.domain.fleet.model.FleetItemResult
import id.bluebird.vsm.domain.user.GetUserByIdForAssignmentState
import id.bluebird.vsm.feature.queue_car_fleet.TestCoroutineRule
import id.bluebird.vsm.feature.queue_car_fleet.main.QueueCarFleetViewModelTest
import id.bluebird.vsm.feature.queue_car_fleet.model.CarFleetItem
import id.bluebird.vsm.feature.queue_car_fleet.request_fleet.RequestCarFleetDialogState
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineRule::class)
internal class DepositionFleetViewModelTest {
    private lateinit var _vm: DepositionFleetViewModel
    private val _getFleet: GetListFleet = mockk(relaxed = true)
    private val error = "error"


    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        _vm = DepositionFleetViewModel(_getFleet)
    }

    @Test
    fun `initTest when list is not empty and not access endpoint`() = runTest {
        //given
        val events = mutableListOf<DepositionFleetState>()
        val fleetList = arrayListOf(
            CarFleetItem(
                id = 1L,
                name = "aa",
                arriveAt = "cc"
            )
        )
        _vm.setListFleet(fleetList)
        // Execute
        val job = launch {
            _vm.actionState.toList(events)
        }
        _vm.init(1, 2)
        runCurrent()
        job.cancel()

        // Result
        Assertions.assertEquals(2, events.size)
        Assertions.assertEquals(
            DepositionFleetState.ProgressGetList,
            events[0]
        )
        Assertions.assertEquals(
            DepositionFleetState.GetListSuccess(
                fleetList.toList()
            ),
            events[1]
        )
    }

    @Test
    fun `initTest when get list is failed `() = runTest {
        //given
        val events = mutableListOf<DepositionFleetState>()
        val result = Throwable(message = error)

        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { _getFleet.invoke(any()) } returns flow {
            throw result
        }

        // Execute
        val job = launch {
            _vm.actionState.toList(events)
        }
        _vm.init(1, 2)
        runCurrent()
        job.cancel()

        // Result
        Assertions.assertEquals(2, events.size)
        Assertions.assertEquals(
            DepositionFleetState.ProgressGetList,
            events[0]
        )
        Assertions.assertEquals(
            DepositionFleetState.FailedGetList(result),
            events[1]
        )
    }

    @Test
    fun `initTest when get list is success with condition empty `() = runTest {
        //given
        val events = mutableListOf<DepositionFleetState>()

        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { _getFleet.invoke(any()) } returns flow {
            emit(
                GetListFleetState.EmptyResult
            )
        }

        // Execute
        val job = launch {
            _vm.actionState.toList(events)
        }
        _vm.init(1, 2)
        runCurrent()
        job.cancel()

        // Result
        Assertions.assertEquals(2, events.size)
        Assertions.assertEquals(
            DepositionFleetState.ProgressGetList,
            events[0]
        )
        Assertions.assertEquals(
            DepositionFleetState.GetListEmpty,
            events[1]
        )
    }


    @Test
    fun `initTest when get list is success with condition list not empty `() = runTest {
        //given
        val events = mutableListOf<DepositionFleetState>()
        val timeArrived = "2022-07-19T01:03:13Z"

        // Mock
        every { Hawk.get<Long>(any()) } returns 1L
        every { _getFleet.invoke(any()) } returns flow {
            emit(
                GetListFleetState.Success(
                    listOf(
                        FleetItemResult(
                            fleetId = 1L,
                            fleetName = "aa",
                            arriveAt = timeArrived
                        )
                    )
                )
            )
        }

        // Execute
        val job = launch {
            _vm.actionState.toList(events)
        }
        _vm.init(1, 2)
        runCurrent()
        job.cancel()

        // Result
        Assertions.assertEquals(2, events.size)
        Assertions.assertEquals(
            DepositionFleetState.ProgressGetList,
            events[0]
        )
        Assertions.assertEquals(
            DepositionFleetState.GetListSuccess(
                listOf(
                    CarFleetItem(
                        1L,
                        "aa",
                        timeArrived.convertCreateAtValue()
                    )
                )
            ),
            events[1]
        )
    }
}