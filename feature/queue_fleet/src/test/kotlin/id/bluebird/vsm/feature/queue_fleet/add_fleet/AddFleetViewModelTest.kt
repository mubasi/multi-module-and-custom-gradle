package id.bluebird.vsm.feature.queue_fleet.add_fleet

import com.orhanobut.hawk.Hawk
import id.bluebird.vsm.core.utils.hawk.UserUtils
import id.bluebird.vsm.domain.fleet.SearchFleetState
import id.bluebird.vsm.domain.fleet.domain.cases.AddFleet
import id.bluebird.vsm.domain.fleet.domain.cases.SearchFleet
import id.bluebird.vsm.domain.fleet.model.FleetItemResult
import id.bluebird.vsm.domain.passenger.WaitingQueueState
import id.bluebird.vsm.domain.passenger.domain.cases.SearchWaitingQueue
import id.bluebird.vsm.domain.passenger.model.Queue
import id.bluebird.vsm.feature.queue_fleet.TestCoroutineRule
import id.bluebird.vsm.feature.queue_fleet.model.FleetItem
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
internal class AddFleetViewModelTest {

    companion object {

        private const val ERROR = "error"
    }

    private lateinit var _vm: AddFleetViewModel
    private val _addFleet: AddFleet = mockk()
    private val _searchFleet: SearchFleet = mockk()
    private val _searchWaitingQueue: SearchWaitingQueue = mockk()
    private val _events = mutableListOf<AddFleetState>()
    @BeforeEach
    fun setup() {
        mockkStatic(Hawk::class)
        _vm = AddFleetViewModel(
            searchFleet = _searchFleet,
            addFleet = _addFleet,
            searchWaitingQueue = _searchWaitingQueue
        )
    }
    @AfterEach
    fun resetEvent() {
        _events.clear()
    }
    @Test
    fun searchQueueTestSuccess() = runTest {
        //given
        val queueList: MutableList<Queue> = mutableListOf()
        queueList.add(
            Queue(
                1,
                "123",
                "aaa",
                "aaa",
                "aaa",
                1,
                "aaa",
                11
            )
        )
        _vm.setParams("aaa")
        _vm.setSubLocation(11)
        _vm.setLocationId(1)

        every {
            _searchWaitingQueue.invoke(
                queueNumber = any(), any(), any()
            )
        } returns flow {
            emit(
                WaitingQueueState.Success(
                    waitingQueue = queueList
                )
            )
        }
        // Execute
        val job = launch {
            _vm.addFleetState.toList(_events)
        }
        _vm.searchQueue()
        runCurrent()
        job.cancel()
        // Result
        Assertions.assertEquals(1, _events.size)
        Assertions.assertEquals(AddFleetState.GetListEmpty, _events.last())
    }
    @Test
    fun searchQueueTestFailed() = runTest {
        //given
        val result = Throwable()
        val queueList: MutableList<Queue> = mutableListOf()
        queueList.add(
            Queue(
                1,
                "123",
                "aaa",
                "aaa",
                "aaa",
                1,
                "aaa",
                11
            )
        )
        _vm.setParams("aaa")
        _vm.setSubLocation(11)
        _vm.setLocationId(1)

        every {
            _searchWaitingQueue.invoke(
                queueNumber = "aaa", 1, 11
            )
        } returns flow {
            throw NullPointerException(ERROR)
        }
        // Execute
        val job = launch {
            _vm.addFleetState.toList(_events)
        }
        _vm.searchQueue()
        runCurrent()
        job.cancel()
        // Result
        Assertions.assertEquals(1, _events.size)
    }
    @Test
    fun `updateSelectedFleetNumber, given selectedFleetNumber , result selectedFleetNumber is empty`() =
        runTest {
            // Given
            val fleetNumber = "BB1212"
            _vm.selectedFleetNumber.value = fleetNumber
            // Pre
            Assertions.assertEquals(fleetNumber, _vm.selectedFleetNumber.value)
            // Execute
            _vm.updateSelectedFleetNumber(fleetNumber, 1)
            // Result
            Assertions.assertEquals("", _vm.selectedFleetNumber.value)
        }
    @Test
    fun `addFleet, finish if isSearchQueue true`() = runTest {
        //given
        _vm.setIsSearchQueue(true)
        _vm.selectedFleetNumber.value = "aa"
        // Execute
        val job = launch {
            _vm.addFleetState.toList(_events)
        }
        _vm.addFleet()
        runCurrent()
        job.cancel()
        // Result
        Assertions.assertEquals(1, _events.size)
        Assertions.assertEquals(AddFleetState.FinishSelectQueue("aa"), _events.last())
    }
    @Test
    fun `addFleet, finish if isSearchQueue false, and set fleetNumber, and success`() = runTest {
        //given
        _vm.setIsSearchQueue(false)
        _vm.selectedFleetNumber.value = "aa"
        _vm.setSubLocation(1)
        _vm.setLocationId(10)
        _vm.setParams("aa")

        every { UserUtils.getLocationId() } returns 10
        every {
            _addFleet.invoke(
                fleetNumber = "aa",
                subLocationId = 1,
                locationId = 10
            )
        } returns flow {
            emit(
                id.bluebird.vsm.domain.fleet.AddFleetState.Success(
                    FleetItemResult(
                        1,
                        "aa",
                        "2022-01-01"
                    )
                )
            )
        }
        // Execute
        val job = launch {
            _vm.addFleetState.toList(_events)
        }
        _vm.addFleet()
        runCurrent()
        job.cancel()
        // Result
        Assertions.assertEquals(1, _events.size)
        Assertions.assertEquals(
            AddFleetState.AddFleetSuccess(
                FleetItem(
                    id = 1,
                    name = "aa",
                    arriveAt = "01 Jan 2022 . 07:00"
                )
            ), _events.last()
        )
    }
    @Test
    fun `addFleet, finish if isSearchQueue false, and set fleetNumber, and failed`() = runTest {
        //given
        _vm.setIsSearchQueue(false)
        _vm.selectedFleetNumber.value = "aa"
        _vm.param.value = null
        _vm.setSubLocation(1)
        val result = Throwable()

        every {
            UserUtils.getLocationId()
        } returns 10L
        every {
            _addFleet.invoke(
                fleetNumber = any(),
                subLocationId = any(),
                locationId = any()
            )
        } returns flow {
            throw result //NullPointerException(ERROR)
        }
        // Execute
        val job = launch {
            _vm.addFleetState.toList(_events)
        }
        _vm.addFleet()
        runCurrent()
        job.cancel()
        // Result
        Assertions.assertEquals(1, _events.size)
        Assertions.assertEquals(AddFleetState.AddError(result), _events.last())
    }
    @Test
    fun searchFleetTestSuccess() = runTest {
        //given
        _vm.setIsSearchQueue(false)
        _vm.setParams("aaa")
        _vm.setSubLocation(1)
        val queueList: MutableList<Queue> = mutableListOf()
        val listFleet: MutableList<String> = mutableListOf()
        listFleet.add(0, "aa")

        queueList.add(
            Queue(
                1,
                "123",
                "aaa",
                "aaa",
                "aaa",
                1,
                "aaa",
                1
            )
        )

        every { _searchFleet.invoke("aaa") } returns flow {
            emit(
                SearchFleetState.Success(
                    listFleet
                )
            )
        }
        // Execute
        val job = launch {
            _vm.addFleetState.toList(_events)
        }
        _vm.searchFleet()
        runCurrent()
        job.cancel()
        // Result
        Assertions.assertEquals(1, _events.size)
        Assertions.assertEquals(AddFleetState.GetListEmpty, _events.last())
    }
    @Test
    fun `initTest, check val search and sublocation`() = runTest {
        val isSearchQueue = true

        _vm.init(locationId = 1, subLocationId = 11, isSearchQueue = isSearchQueue)

        Assertions.assertEquals(11, _vm.valSubLocationId())
        Assertions.assertEquals(1, _vm.valLocationId())
        Assertions.assertEquals(true, _vm.valIsSearchQueue())
    }

}